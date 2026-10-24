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


def run(code: str, stdin_text: str = "", timeout: float = 30.0, plot_dir: str = "") -> str:
    out, err = io.StringIO(), io.StringIO()
    res = {"stdout": "", "stderr": "", "elapsed_ms": 0, "ok": True, "timed_out": False, "plots": []}
    ns = {"__name__": "__main__", "__file__": "main.py"}

    def target():
        try:
            exec(compile(code, "main.py", "exec"), ns)
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
            t.join(timeout)
            if t.is_alive():
                res["timed_out"] = True
                res["ok"] = False
                _stop_thread(t)
                t.join(2)
                err.write(f"\n⏱ Execution exceeded {timeout:.0f}s and was stopped.\n")
    finally:
        sys.stdin = old_in
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
