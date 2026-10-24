"""Line runtime bridge: sandboxed-ish execution, hard timeout, JSON results."""
import contextlib, ctypes, io, json, os, sys, threading, time, traceback

import warnings
os.environ.setdefault("MPLBACKEND", "Agg")
warnings.filterwarnings("ignore", message=".*non-interactive.*")


def _stop_thread(t: threading.Thread) -> None:
    """Raise SystemExit inside a running thread (stops most pure-Python loops)."""
    if t.ident is not None:
        ctypes.pythonapi.PyThreadState_SetAsyncExc(
            ctypes.c_ulong(t.ident), ctypes.py_object(SystemExit))


def _make_input(waited):
    """input(): use pre-filled stdin lines first, otherwise ask the user through the app dialog."""
    try:
        from java import jclass
        bridge = jclass("com.line.ide.core.InputBridge")
    except Exception:
        bridge = None

    def _input(prompt=""):
        sys.stdout.write(str(prompt))
        sys.stdout.flush()
        line = sys.stdin.readline()
        if line != "":
            ans = line.rstrip("\n")
        elif bridge is None:
            raise EOFError("EOF when reading a line")
        else:
            t0 = time.perf_counter()
            ans = str(bridge.request(str(prompt)))
            waited[0] += time.perf_counter() - t0
        sys.stdout.write(ans + "\n")
        return ans
    return _input


def _save_plots(plot_dir: str) -> list:
    paths = []
    plt = sys.modules.get("matplotlib.pyplot")
    if plt is None or not plot_dir:
        return paths
    os.makedirs(plot_dir, exist_ok=True)
    for n in plt.get_fignums():
        p = os.path.join(plot_dir, f"plot_{n}.png")
        plt.figure(n).savefig(p, dpi=110, bbox_inches="tight")
        paths.append(p)
    plt.close("all")
    return paths


def run(code: str, stdin_text: str = "", timeout: float = 30.0, plot_dir: str = "", workspace: str = "", trace: bool = False) -> str:
    out, err = io.StringIO(), io.StringIO()
    res = {"stdout": "", "stderr": "", "elapsed_ms": 0, "ok": True, "timed_out": False, "plots": []}
    waited = [0.0]
    steps = []
    if workspace:
        os.makedirs(workspace, exist_ok=True)
        os.chdir(workspace)
        for p in (os.path.join(workspace, "_libs"), workspace):
            if p not in sys.path:
                sys.path.insert(0, p)

    def _tracer(frame, event, arg):
        if frame.f_code.co_filename != "main.py" or len(steps) >= 400:
            return None
        if event == "line":
            loc = {k: repr(v)[:30] for k, v in frame.f_locals.items() if not k.startswith("__") and not callable(v)}
            steps.append(f"L{frame.f_lineno}: " + ", ".join(f"{k}={v}" for k, v in list(loc.items())[:6]))
        return _tracer
    ns = {"__name__": "__main__", "__file__": "main.py"}
    ns["input"] = _make_input(waited)

    def target():
        try:
            if trace:
                sys.settrace(_tracer)
            try:
                exec(compile(code, "main.py", "exec"), ns)
            finally:
                sys.settrace(None)
            res["plots"] = _save_plots(plot_dir)
        except SystemExit as e:
            if e.code not in (None, 0) and not res["timed_out"]:
                err.write(f"\nProcess exited with code {e.code}\n")
                res["ok"] = False
        except BaseException:
            res["ok"] = False
            tb = traceback.format_exc().splitlines()
            # hide the bridge frame from the traceback
            tb = [l for l in tb if "line_runner.py" not in l and "exec(compile" not in l]
            err.write("\n".join(tb) + "\n")

    old_in = sys.stdin
    sys.stdin = io.StringIO(stdin_text)
    start = time.perf_counter()
    try:
        with contextlib.redirect_stdout(out), contextlib.redirect_stderr(err):
            t = threading.Thread(target=target, daemon=True)
            t.start()
            while t.is_alive() and (time.perf_counter() - start - waited[0]) < timeout:
                t.join(0.1)
            if t.is_alive():
                res["timed_out"] = True
                res["ok"] = False
                _stop_thread(t)
                t.join(2)
                err.write(f"\n⏱ Execution exceeded {timeout:.0f}s and was stopped.\n")
    finally:
        sys.stdin = old_in
    res["trace"] = "\n".join(steps)
    res["elapsed_ms"] = int((time.perf_counter() - start) * 1000)
    res["stdout"], res["stderr"] = out.getvalue(), err.getvalue()
    return json.dumps(res)


def check_syntax(code: str) -> str:
    try:
        compile(code, "main.py", "exec")
        return json.dumps({"ok": True})
    except SyntaxError as e:
        return json.dumps({"ok": False, "line": e.lineno or 0, "msg": e.msg})


def python_version() -> str:
    return sys.version.split()[0]


def tidy(code: str) -> str:
    """Light formatter: tabs->spaces, trim trailing spaces, max 2 blank lines, final newline."""
    out, blanks = [], 0
    for line in code.splitlines():
        line = line.rstrip().replace("\t", "    ")
        if line == "":
            blanks += 1
            if blanks > 2:
                continue
        else:
            blanks = 0
        out.append(line)
    return "\n".join(out).strip("\n") + "\n"


def pip_install(name: str, workspace: str) -> str:
    """Install a pure-Python package from PyPI into <workspace>/_libs (no dependency resolution)."""
    import io, re, urllib.request, zipfile
    if not re.fullmatch(r"[A-Za-z0-9._-]+", name or ""):
        return "Enter a valid package name."
    libs = os.path.join(workspace, "_libs")
    os.makedirs(libs, exist_ok=True)
    try:
        with urllib.request.urlopen(f"https://pypi.org/pypi/{name}/json", timeout=20) as r:
            data = json.load(r)
        whl = next((f for f in data["urls"] if f["packagetype"] == "bdist_wheel" and f["filename"].endswith("none-any.whl")), None)
        if whl is None:
            return f"'{name}' has no pure-Python wheel (it needs native code), so it can't be installed here."
        with urllib.request.urlopen(whl["url"], timeout=60) as r:
            blob = r.read()
        zipfile.ZipFile(io.BytesIO(blob)).extractall(libs)
        if libs not in sys.path:
            sys.path.insert(0, libs)
        return f"Installed {name} {data['info']['version']}. Dependencies are not installed automatically."
    except Exception as e:
        return f"Could not install {name}: {e}"
