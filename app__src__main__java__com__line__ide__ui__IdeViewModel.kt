package com.line.ide.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.line.ide.ai.AiAction
import com.line.ide.ui.theme.LineColors
import com.line.ide.ui.theme.PALETTES
import com.line.ide.ai.OpenRouterClient
import com.line.ide.core.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val TEMPLATES = mapOf(
    "Hello" to "name = input(\"Your name: \") or \"World\"\nprint(f\"Hello, {name}!\")\n",
    "Data (pandas)" to "import pandas as pd\n\ndf = pd.DataFrame({\"x\": range(5), \"y\": [i**2 for i in range(5)]})\nprint(df.describe())\n",
    "Math (sympy)" to "from sympy import symbols, diff, integrate, sin\n\nx = symbols('x')\nprint(diff(x**3 * sin(x), x))\nprint(integrate(x**2, x))\n",
    "Operators demo" to "a, b = 7, 3\nprint(\"a == b:\", a == b)\nprint(\"a != b:\", a != b)\nprint(\"a > b:\", a > b)\nprint(\"a < b:\", a < b)\nprint(\"a >= b:\", a >= b)\nprint(\"a <= b:\", a <= b)\nprint(\"chained 1 < b < a:\", 1 < b < a)\n",
    "Calculator" to "a = float(input(\"First number: \"))\nop = input(\"Operator (+ - * /): \")\nb = float(input(\"Second number: \"))\nops = {\"+\": a + b, \"-\": a - b, \"*\": a * b, \"/\": a / b if b else \"n/a\"}\nprint(\"Result:\", ops.get(op, \"unknown operator\"))\n",
    "Classes (OOP)" to "class Animal:\n    def __init__(self, name):\n        self.name = name\n\n    def speak(self):\n        return f\"{self.name} makes a sound\"\n\nclass Dog(Animal):\n    def speak(self):\n        return f\"{self.name} says Woof!\"\n\nprint(Dog(\"Rex\").speak())\n",
    "Algorithms" to "def fib(n):\n    a, b = 0, 1\n    for _ in range(n):\n        a, b = b, a + b\n    return a\n\nprint([fib(i) for i in range(15)])\n",
    "Plot (matplotlib)" to "import matplotlib.pyplot as plt\nimport numpy as np\n\nx = np.linspace(0, 10, 200)\nplt.plot(x, np.sin(x), label=\"sin\")\nplt.legend()\nplt.show()\n",
    "Rich output" to "from rich.console import Console\nfrom rich.table import Table\n\nt = Table(title=\"Line\")\nt.add_column(\"Feature\"); t.add_column(\"Status\")\nt.add_row(\"Run Python\", \"OK\")\nConsole().print(t)\n"
)

val SNIPPETS = linkedMapOf(
    "with" to "with open(\"file.txt\") as f:\n    data = f.read()\n", "list" to "[x for x in items if x]",
    "lambda" to "lambda x: x", "json" to "import json\nprint(json.dumps(data, indent=2))\n",
    "req" to "import requests\nr = requests.get(\"https://api.github.com\")\nprint(r.status_code, r.json())\n",
    "plot" to "import matplotlib.pyplot as plt\nplt.plot([1, 2, 3], [1, 4, 9])\nplt.show()\n",
    "def" to "def name(args):\n    ", "for" to "for i in range(10):\n    ",
    "class" to "class Name:\n    def __init__(self):\n        ", "try" to "try:\n    \nexcept Exception as e:\n    print(e)\n",
    "main" to "def main():\n    \n\nif __name__ == \"__main__\":\n    main()\n"
)

class IdeViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = FileRepository(app)
    val prefs = SecurePrefs(app)

    val files = mutableStateListOf<String>()
    var current by mutableStateOf("main.py")
    var editor by mutableStateOf(TextFieldValue(""))
    var stdin by mutableStateOf("")
    var output by mutableStateOf("")
    var isError by mutableStateOf(false)
    var status by mutableStateOf("Ready · Python ${runCatching { PythonEngine.version }.getOrDefault("?")}")
    var running by mutableStateOf(false)
    var errorLine by mutableStateOf(0)
    var aiText by mutableStateOf("")
    var aiBusy by mutableStateOf(false)
    val plots = mutableStateListOf<String>()
    val tabs = mutableStateListOf<String>()
    val history = mutableStateListOf<String>()
    var aiQuestion by mutableStateOf("")
    var showOutput by mutableStateOf(false)
    var trace by mutableStateOf("")
    var pipLog by mutableStateOf("")
    var pipBusy by mutableStateOf(false)
    private var wantTrace = false
    var inputPrompt by mutableStateOf<String?>(null)
    var inputSeq by mutableStateOf(0)
    var outTab by mutableStateOf(0)
    var fontSize by mutableStateOf(prefs.fontSize)
    var showFind by mutableStateOf(false)
    var findText by mutableStateOf("")
    var replaceText by mutableStateOf("")
    private val undo = mutableListOf<TextFieldValue>()
    private val redo = mutableListOf<TextFieldValue>()
    private var checkJob: Job? = null
    private var saveJob: Job? = null

    init {
        InputBridge.onPrompt = { p -> inputSeq += 1; inputPrompt = p }
        LineColors.p = PALETTES.getOrElse(prefs.theme) { PALETTES[0] }
        LineColors.animate = prefs.animate
        if (repo.list().isEmpty()) repo.save("main.py", TEMPLATES.getValue("Hello"))
        refresh(); open(repo.list().first())
    }

    private fun refresh() { files.clear(); files.addAll(repo.list()) }

    fun open(name: String) { save(); if (name !in tabs) tabs.add(name); current = name; editor = TextFieldValue(repo.read(name)); errorLine = 0 }
    fun save() = repo.save(current, editor.text)

    fun newFile(name: String, template: String) {
        val n = repo.normalize(name.ifBlank { "untitled" })
        if (!repo.exists(n)) repo.save(n, TEMPLATES[template] ?: "")
        refresh(); open(n)
    }
    fun delete(name: String) {
        repo.delete(name); tabs.remove(name); refresh()
        if (files.isEmpty()) repo.save("main.py", "").also { refresh() }
        if (name == current) open(files.first())
    }
    fun rename(old: String, new: String) {
        if (repo.rename(old, new)) { if (old == current) current = repo.normalize(new); refresh() }
    }

    fun onEdit(n: TextFieldValue) {
        val o = editor
        var v = n
        if (n.text != o.text) { undo.add(o); if (undo.size > 200) undo.removeAt(0); redo.clear() }
        if (n.text.length == o.text.length + 1 && n.selection.collapsed) {          // smart auto-indent
            val i = n.selection.start - 1
            if (i >= 0 && n.text[i] == '\n') {
                val prev = n.text.substring(0, i).substringAfterLast('\n')
                var ind = prev.takeWhile { it == ' ' }
                if (prev.trimEnd().endsWith(":")) ind += "    "
                v = TextFieldValue(n.text.substring(0, i + 1) + ind + n.text.substring(i + 1), TextRange(i + 1 + ind.length))
            }
        }
        if (v === n && n.text.length == o.text.length + 1 && n.selection.collapsed && n.selection.start >= 1) {   // auto-close brackets
            val i = n.selection.start - 1
            val ch = n.text[i]
            val close = when (ch) { '(' -> ")"; '[' -> "]"; '{' -> "}"; '"' -> "\""; '\'' -> "'"; else -> "" }
            val afterWord = i > 0 && n.text[i - 1].isLetterOrDigit()
            if (close.isNotEmpty() && !((ch == '"' || ch == '\'') && afterWord))
                v = TextFieldValue(n.text.substring(0, i + 1) + close + n.text.substring(i + 1), TextRange(i + 1))
        }
        editor = v
        checkJob?.cancel()
        checkJob = viewModelScope.launch { delay(500); errorLine = PythonEngine.check(editor.text)?.line ?: 0 }
        saveJob?.cancel()
        saveJob = viewModelScope.launch { delay(1500); save() }
    }

    fun insert(s: String) {
        val e = editor; val a = e.selection.min; val b = e.selection.max
        val cursor = if (s == "()" || s == "[]" || s == "{}" || s == "\"\"") 1 else s.length
        editor = TextFieldValue(e.text.replaceRange(a, b, s), TextRange(a + cursor))
    }

    fun run() {
        if (running) return
        save(); running = true; status = "Running…"; output = ""; showOutput = true; outTab = 0
        viewModelScope.launch {
            val dir = getApplication<Application>().cacheDir.resolve("plots").apply { deleteRecursively(); mkdirs() }
            val r = PythonEngine.run(editor.selection.let { if (it.collapsed) editor.text else editor.text.substring(it.min, it.max) }, stdin, prefs.timeout.toDouble(), dir.absolutePath, repo.path, wantTrace)
            plots.clear(); plots.addAll(r.plots)
            trace = r.trace; wantTrace = false
            if (r.trace.isNotBlank()) outTab = 4
            output = (r.stdout + r.stderr).ifBlank { if (r.plots.isEmpty()) "(no output)" else "" }
            isError = !r.ok
            status = (if (r.ok) "✓ Finished" else if (r.timedOut) "⏱ Timed out" else "✗ Failed") + " in ${r.elapsedMs} ms"
            history.add(0, "${java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.US).format(java.util.Date())} · $current · $status\n${output.take(80)}")
            if (history.size > 30) history.removeAt(history.lastIndex)
            running = false
        }
    }

    fun ask(action: AiAction) {
        if (aiBusy) return
        aiBusy = true; aiText = "Thinking…"
        viewModelScope.launch {
            val extra = (if (action == AiAction.ASK) "Question: $aiQuestion\n" else "") + (if (isError) "Last error:\n$output" else "")
            aiText = OpenRouterClient.ask(prefs.apiKey, prefs.model, action, editor.text, extra)
            aiBusy = false
        }
    }

    fun undo() { if (undo.isNotEmpty()) { redo.add(editor); editor = undo.removeAt(undo.lastIndex) } }
    fun redo() { if (redo.isNotEmpty()) { undo.add(editor); editor = redo.removeAt(redo.lastIndex) } }

    /** Autocomplete: keywords, builtins and every identifier already in the file. */
    fun suggestions(): List<String> {
        val t = editor.text; val c = editor.selection.start.coerceIn(0, t.length)
        val p = t.substring(0, c).takeLastWhile { it.isLetterOrDigit() || it == '_' }
        if (p.length < 2) return emptyList()
        val words = Regex("[A-Za-z_]\\w{2,}").findAll(t).map { it.value }
        return (com.line.ide.ui.editor.SyntaxHighlighter.keywords + com.line.ide.ui.editor.SyntaxHighlighter.builtins + words)
            .filter { it.startsWith(p) && it != p }.distinct().take(6)
    }
    fun accept(w: String) {
        val e = editor; val c = e.selection.start
        val p = e.text.substring(0, c).takeLastWhile { it.isLetterOrDigit() || it == '_' }
        editor = TextFieldValue(e.text.replaceRange(c - p.length, c, w), TextRange(c - p.length + w.length))
    }

    fun findNext() {
        if (findText.isEmpty()) return
        val t = editor.text
        var i = t.indexOf(findText, editor.selection.max, ignoreCase = true)
        if (i < 0) i = t.indexOf(findText, 0, ignoreCase = true)
        if (i >= 0) editor = editor.copy(selection = TextRange(i, i + findText.length))
        status = if (i >= 0) "Found at char $i" else "Not found"
    }
    fun replaceAll() {
        if (findText.isEmpty()) return
        val n = Regex(Regex.escape(findText), RegexOption.IGNORE_CASE).findAll(editor.text).count()
        onEdit(TextFieldValue(editor.text.replace(findText, replaceText, ignoreCase = true)))
        status = "Replaced $n"
    }

    fun submitInput(s: String) { inputPrompt = null; InputBridge.answer(s) }

    fun closeTab(t: String) { if (tabs.size > 1) { tabs.remove(t); if (t == current) open(tabs.last()) } }
    fun tidy() { viewModelScope.launch { onEdit(TextFieldValue(PythonEngine.tidy(editor.text))); status = "Code tidied" } }
    fun importCode(name: String, text: String) { newFile(name, ""); onEdit(TextFieldValue(text)); save() }
    fun setTheme(i: Int) { prefs.theme = i; LineColors.p = PALETTES[i] }

    fun toggleComment() {
        val t = editor.text; val a = editor.selection.min; val b = editor.selection.max
        val s = t.lastIndexOf('\n', a - 1) + 1
        val e = t.indexOf('\n', b).let { if (it < 0) t.length else it }
        val lines = t.substring(s, e).split("\n")
        val allCommented = lines.all { it.isBlank() || it.trimStart().startsWith("#") }
        val out = lines.joinToString("\n") { l ->
            if (l.isBlank()) l else if (allCommented) l.replaceFirst(Regex("# ?"), "") else "# $l"
        }
        onEdit(TextFieldValue(t.replaceRange(s, e, out), TextRange(s, s + out.length)))
    }

    fun duplicateLine() {
        val t = editor.text; val c = editor.selection.start.coerceIn(0, t.length)
        val s = t.lastIndexOf('\n', c - 1) + 1
        val e = t.indexOf('\n', c).let { if (it < 0) t.length else it }
        val line = t.substring(s, e)
        onEdit(TextFieldValue(t.substring(0, e) + "\n" + line + t.substring(e), TextRange(c + line.length + 1)))
    }

    fun deleteLine() {
        val t = editor.text; val c = editor.selection.start.coerceIn(0, t.length)
        val s = t.lastIndexOf('\n', c - 1) + 1
        val e = t.indexOf('\n', c).let { if (it < 0) t.length else it }
        val from = if (e >= t.length && s > 0) s - 1 else s
        val to = if (e < t.length) e + 1 else e
        val nt = t.removeRange(from, to)
        onEdit(TextFieldValue(nt, TextRange(from.coerceIn(0, nt.length))))
    }

    fun outline(): List<Pair<Int, String>> = editor.text.lines().mapIndexedNotNull { i, l ->
        if (Regex("^\\s*(def|class)\\s+\\w+").containsMatchIn(l)) (i + 1) to l.trim().removeSuffix(":") else null
    }

    fun jumpTo(line: Int) {
        val t = editor.text
        var idx = 0
        repeat(line - 1) { val n = t.indexOf('\n', idx); if (n >= 0) idx = n + 1 }
        editor = editor.copy(selection = TextRange(idx))
    }

    fun runWithTrace() { wantTrace = true; run() }

    fun installPackage(name: String) {
        if (pipBusy) return
        pipBusy = true; pipLog = "Downloading…"
        viewModelScope.launch { pipLog = PythonEngine.pip(name.trim(), repo.path); pipBusy = false }
    }
}
