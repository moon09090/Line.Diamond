package com.line.ide.ui.screens

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import com.line.ide.ui.theme.category
import androidx.compose.foundation.Image
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.line.ide.ai.AiAction
import com.line.ide.ui.IdeViewModel
import com.line.ide.ui.SNIPPETS
import com.line.ide.ui.TEMPLATES
import com.line.ide.ui.editor.CodeEditor
import com.line.ide.ui.theme.LineColors
import com.line.ide.ui.theme.ElementBackground
import com.line.ide.ui.theme.PALETTES

private fun Modifier.glass(): Modifier = this
    .clip(RoundedCornerShape(14.dp))
    .background(LineColors.Glass)
    .border(1.dp, Brush.linearGradient(listOf(LineColors.Gold.copy(alpha = 0.45f), LineColors.Violet.copy(alpha = 0.45f))), RoundedCornerShape(14.dp))

@Composable
private fun AuroraBackground(content: @Composable () -> Unit) {
    val t = rememberInfiniteTransition(label = "aurora")
    val p by t.animateFloat(0.15f, 0.85f, infiniteRepeatable(tween(25000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "p")
    Box(Modifier.fillMaxSize().background(LineColors.Obsidian).drawBehind {
        drawRect(Brush.radialGradient(listOf(LineColors.p.glow, Color.Transparent),
            center = Offset(size.width * p, size.height * 0.22f), radius = size.width * 1.2f))
    }) { content() }
}

@Composable
private fun InfoDialog(title: String, body: String, onClose: () -> Unit) = AlertDialog(onClose,
    confirmButton = { TextButton(onClose) { Text("Close") } },
    title = { Text(title) },
    text = { Column(Modifier.verticalScroll(rememberScrollState())) {
        SelectionContainer { Text(body, fontFamily = FontFamily.Monospace, fontSize = 13.sp) } } })

@Composable
private fun Splash() {
    Box(Modifier.fillMaxSize().background(LineColors.Obsidian), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            LineLogo(96.dp)
            Spacer(Modifier.height(16.dp))
            Text("Line", color = LineColors.Gold, fontSize = 30.sp, fontWeight = FontWeight.Light, letterSpacing = 6.sp)
            Text("Python, beautifully.", color = LineColors.Muted, fontSize = 13.sp)
        }
    }
}

@Composable
private fun Mascot(mood: Int) {
    val tr = rememberInfiniteTransition(label = "mascot")
    val bob by tr.animateFloat(0f, 1f, infiniteRepeatable(tween(if (mood == 1) 500 else 1800, easing = LinearEasing), RepeatMode.Reverse), label = "bob")
    Canvas(Modifier.size(30.dp)) {
        val c = Offset(size.width / 2f, size.height / 2f + (bob - 0.5f) * 4f)
        val r = size.minDimension * 0.4f
        val ink = Color(0xFF1A1405)
        drawCircle(LineColors.Gold, r, c)
        drawCircle(ink, 2.2f, Offset(c.x - r * 0.35f, c.y - r * 0.15f))
        drawCircle(ink, 2.2f, Offset(c.x + r * 0.35f, c.y - r * 0.15f))
        drawArc(ink, if (mood == 2) 200f else 20f, 140f, false,
            Offset(c.x - r * 0.4f, c.y + (if (mood == 2) r * 0.15f else -r * 0.05f)), Size(r * 0.8f, r * 0.6f),
            style = Stroke(2f, cap = StrokeCap.Round))
    }
}

@Composable
private fun SparkleBurst(trigger: Int) {
    val p = remember { Animatable(1f) }
    LaunchedEffect(trigger) { if (trigger > 0) { p.snapTo(0f); p.animateTo(1f, tween(800)) } }
    Canvas(Modifier.fillMaxSize()) {
        if (p.value < 1f) for (i in 0 until 16) {
            val ang = i * 6.2832f / 16f
            val d = p.value * size.width * 0.35f
            drawCircle((if (i % 2 == 0) LineColors.Gold else LineColors.Violet).copy(alpha = 1f - p.value), 5f * (1f - p.value) + 1f,
                Offset(size.width * 0.82f + kotlin.math.cos(ang) * d, size.height * 0.08f + kotlin.math.sin(ang) * d))
        }
    }
}

@Composable
private fun LineLogo(logoSize: androidx.compose.ui.unit.Dp = 30.dp) {
    val tr = rememberInfiniteTransition(label = "logo")
    val a by tr.animateFloat(0f, 360f, infiniteRepeatable(tween(4000, easing = LinearEasing)), label = "a")
    Canvas(Modifier.size(logoSize)) {
        val c = center
        val r = size.minDimension / 2f - 3f
        drawCircle(LineColors.Gold.copy(alpha = 0.35f), r, c, style = Stroke(2f))
        drawArc(LineColors.Gold, a, 70f, false, Offset(c.x - r, c.y - r), Size(r * 2f, r * 2f), style = Stroke(3f, cap = StrokeCap.Round))
        drawLine(LineColors.Gold, Offset(size.width * 0.3f, size.height * 0.7f), Offset(size.width * 0.7f, size.height * 0.3f),
            strokeWidth = 4f, cap = StrokeCap.Round)
        drawCircle(LineColors.Violet, 3.5f, Offset(size.width * 0.7f, size.height * 0.3f))
        drawCircle(LineColors.Violet, 3.5f, Offset(size.width * 0.3f, size.height * 0.7f))
    }
}

@Composable
private fun RunButton(enabled: Boolean, onClick: () -> Unit) {
    val t = rememberInfiniteTransition(label = "shimmer")
    val x by t.animateFloat(-1f, 2f, infiniteRepeatable(tween(3500, easing = LinearEasing)), label = "x")
    Box(
        Modifier.clip(RoundedCornerShape(10.dp))
            .background(Brush.linearGradient(listOf(Color(0xFFD4AF37), Color(0xFFF5E6A8))))
            .drawWithContent {
                drawContent()
                val w = size.width
                drawRect(Brush.linearGradient(listOf(Color.Transparent, Color.White.copy(alpha = 0.6f), Color.Transparent),
                    start = Offset(x * w, 0f), end = Offset(x * w + w * 0.6f, size.height)))
            }
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) { Text(if (enabled) "▶  Run" else "Running…", color = Color(0xFF1A1405), fontWeight = FontWeight.SemiBold) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdeScreen(vm: IdeViewModel) {
    var showFiles by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showNew by remember { mutableStateOf(false) }
    var menu by remember { mutableStateOf(false) }
    var showThemes by remember { mutableStateOf(false) }
    var showOutline by remember { mutableStateOf(false) }
    var showPip by remember { mutableStateOf(false) }
    var showRename by remember { mutableStateOf(false) }
    var showStats by remember { mutableStateOf(false) }
    var showLint by remember { mutableStateOf(false) }
    var showImport by remember { mutableStateOf(false) }
    var showSnips by remember { mutableStateOf(false) }
    var snipTick by remember { mutableStateOf(0) }
    var tipText by remember { mutableStateOf<String?>(null) }
    var themeCat by remember { mutableStateOf("All") }
    var splash by remember { mutableStateOf(true) }
    var tour by remember { mutableStateOf(if (vm.prefs.onboarded) -1 else 0) }
    var challengeText by remember { mutableStateOf<String?>(null) }
    var showLessons by remember { mutableStateOf(false) }
    var lessonIdx by remember { mutableStateOf(-1) }
    LaunchedEffect(Unit) { kotlinx.coroutines.delay(1600); splash = false }
    LaunchedEffect(vm.cycle) { while (vm.cycle) { kotlinx.coroutines.delay(15000); vm.randomTheme() } }
    val ctx = LocalContext.current
    val clip = LocalClipboardManager.current
    val shareText: (String) -> Unit = { txt ->
        ctx.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, txt), "Share"))
    }
    LaunchedEffect(vm.keepAwake) {
        (ctx as? android.app.Activity)?.window?.let { w ->
            val flag = android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            if (vm.keepAwake) w.addFlags(flag) else w.clearFlags(flag)
        }
    }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { vm.importCode(it.lastPathSegment?.substringAfterLast('/') ?: "imported.py",
            ctx.contentResolver.openInputStream(it)?.bufferedReader()?.readText() ?: "") }
    }

    ElementBackground {
        Box(Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Row(verticalAlignment = Alignment.CenterVertically) {
                        LineLogo()
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(vm.current, fontSize = 16.sp, color = LineColors.Ivory, fontFamily = FontFamily.Monospace)
                            Text(vm.status, fontSize = 11.sp, color = if (vm.isError) LineColors.Error else LineColors.Muted, maxLines = 1)
                        }
                    } },
                    navigationIcon = { IconButton({ showFiles = true }) { Icon(Icons.Default.Folder, null, tint = LineColors.Gold) } },
                    actions = {
                        Mascot(if (vm.running) 1 else if (vm.isError) 2 else 0)
                        TextButton({ vm.showOutput = true }) { Text("Console", color = LineColors.Violet) }
                        Box { IconButton({ menu = true }) { Icon(Icons.Default.MoreVert, "More", tint = LineColors.Muted) }
                            DropdownMenu(menu, { menu = false }) {
                                listOf<Pair<String, (() -> Unit)?>>(
                                    "✏️ EDIT" to null,
                                    "Undo" to { vm.undo() }, "Redo" to { vm.redo() }, "Select all" to { vm.selectAll() },
                                    "Find & replace" to { vm.showFind = !vm.showFind },
                                    "Toggle comment" to { vm.toggleComment() }, "Indent" to { vm.indent() }, "Dedent" to { vm.dedent() },
                                    "Move line up" to { vm.moveUp() }, "Move line down" to { vm.moveDown() },
                                    "Duplicate line" to { vm.duplicateLine() }, "Delete line" to { vm.deleteLine() },
                                    "🔧 TRANSFORM" to null,
                                    "UPPERCASE selection" to { vm.upper() }, "lowercase selection" to { vm.lower() },
                                    "Sort lines" to { vm.sortLines() }, "Remove blank lines" to { vm.removeBlankLines() },
                                    "Wrap selection in print()" to { vm.wrapPrint() },
                                    "Tidy code" to { vm.tidy() }, "Add file header" to { vm.addHeader() },
                                    "🧰 CODE TOOLS" to null,
                                    "Add import…" to { showImport = true }, "Outline (jump to def)" to { showOutline = true },
                                    "Lint (PEP 8 check)" to { showLint = true }, "Code statistics" to { showStats = true },
                                    "Install package (pip)" to { showPip = true },
                                    "▶ RUN & DEBUG" to null,
                                    "Run with trace" to { vm.runWithTrace() },
                                    "Toggle breakpoint (this line)" to { vm.toggleBreakpoint() },
                                    "Clear breakpoints" to { vm.breakpoints.clear() },
                                    "Benchmark (run 5 times)" to { vm.benchmark() },
                                    "Clear output" to { vm.output = ""; vm.plots.clear() },
                                    "Copy output" to { clip.setText(AnnotatedString(vm.output)) },
                                    "Share output" to { shareText(vm.output) },
                                    "📁 FILES & SNIPPETS" to null,
                                    "Rename file" to { showRename = true }, "Duplicate file" to { vm.duplicateFile() },
                                    "Copy all code" to { clip.setText(AnnotatedString(vm.editor.text)) },
                                    "Share code" to { shareText(vm.editor.text) },
                                    "Share code as image" to {
                                        val uri = com.line.ide.core.CodeImage.save(ctx, vm.current, vm.editor.text)
                                        if (uri == null) { vm.status = "Image export needs Android 10 or newer" }
                                        else {
                                            ctx.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).setType("image/png")
                                                .putExtra(Intent.EXTRA_STREAM, uri).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION), "Share image"))
                                        }
                                    },
                                    "My snippets…" to { showSnips = true },
                                    "Save selection as snippet" to { vm.saveSnippet() },
                                    "🎨 VIEW & FUN" to null,
                                    "Themes" to { showThemes = true }, "Random theme" to { vm.randomTheme() },
                                    "Theme slideshow: ${if (vm.cycle) "ON" else "OFF"}" to { vm.cycle = !vm.cycle },
                                    "Font bigger" to { vm.zoom(2) }, "Font smaller" to { vm.zoom(-2) },
                                    "Zen mode (hide extras)" to { vm.zen = !vm.zen },
                                    "Keep screen on: ${if (vm.keepAwake) "ON" else "OFF"}" to { vm.keepAwake = !vm.keepAwake },
                                    "Python tip of the day" to { tipText = com.line.ide.ui.TIPS.random() },
                                    "Learn Python (lessons)" to { showLessons = true },
                                    "Daily challenge" to { challengeText = vm.todaysChallenge() },
                                    "Theme sounds: ${if (vm.sound) "ON" else "OFF"}" to { vm.setSound(!vm.sound) },
                                    "Settings" to { showSettings = true }
                                ).forEach { (l, a) ->
                                    if (a == null) DropdownMenuItem({ Text(l, fontSize = 11.sp, color = LineColors.Gold) }, {}, enabled = false)
                                    else DropdownMenuItem({ Text(l) }, { menu = false; a() })
                                }
                            } }
                        RunButton(!vm.running) { vm.run() }
                        Spacer(Modifier.width(8.dp))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { pad ->
            Column(Modifier.padding(pad).fillMaxSize().imePadding()) {
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 6.dp)) {
                    vm.tabs.forEach { t ->
                        InputChip(t == vm.current, { vm.open(t) }, { Text(t, fontSize = 12.sp) }, Modifier.padding(2.dp),
                            trailingIcon = { Icon(Icons.Default.Close, "close", Modifier.size(16.dp).clickable { vm.closeTab(t) }) })
                    }
                }
                if (vm.showFind) Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp).glass().padding(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedTextField(vm.findText, { vm.findText = it }, Modifier.weight(1f), singleLine = true, label = { Text("Find") })
                    OutlinedTextField(vm.replaceText, { vm.replaceText = it }, Modifier.weight(1f), singleLine = true, label = { Text("Replace") })
                    Column { TextButton({ vm.findNext() }) { Text("Next") }; TextButton({ vm.replaceAll() }) { Text("All") } }
                }
                CodeEditor(vm.editor, vm.errorLine, vm.fontSize, vm.breakpoints.toSet(), vm::onEdit,
                    Modifier.weight(1f).fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp).glass())
                val cur = vm.editor.selection.start.coerceIn(0, vm.editor.text.length)
                val ln = vm.editor.text.substring(0, cur).count { it == '\n' } + 1
                val col = cur - vm.editor.text.substring(0, cur).lastIndexOf('\n')
                if (!vm.zen) Text("Ln $ln, Col $col  ·  ${vm.editor.text.count { it == '\n' } + 1} lines  ·  ${vm.editor.text.length} chars  ·  Lv ${vm.level}",
                    Modifier.padding(horizontal = 14.dp), color = LineColors.Muted, fontSize = 11.sp)
                if (!vm.zen) Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 4.dp)) {
                    listOf(" == ", " != ", " < ", " > ", " <= ", " >= ", " + ", " - ", " * ", " / ", " // ", " % ", " ** ",
                        " and ", " or ", " not ", " in ", " is ").forEach { k ->
                        TextButton({ vm.insert(k) }, contentPadding = PaddingValues(horizontal = 9.dp)) {
                            Text(k.trim(), color = LineColors.Violet, fontFamily = FontFamily.Monospace) }
                    }
                }
                if (!vm.zen) Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(4.dp)) {
                    vm.suggestions().forEach { w ->
                        AssistChip({ vm.accept(w) }, { Text(w, fontFamily = FontFamily.Monospace) }, Modifier.padding(horizontal = 2.dp))
                    }
                    SNIPPETS.forEach { (k, v) -> TextButton({ vm.insert(v) }, contentPadding = PaddingValues(horizontal = 8.dp)) {
                        Text(k, color = LineColors.Ivory, fontFamily = FontFamily.Monospace) } }
                    listOf("    ", ":", "()", "[]", "{}", "\"\"", "=", "#", "_", "'").forEach { k ->
                        TextButton({ vm.insert(k) }, contentPadding = PaddingValues(horizontal = 10.dp)) {
                            Text(if (k == "    ") "TAB" else k, color = LineColors.Gold, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
        SparkleBurst(vm.successTick)
        AnimatedVisibility(splash, exit = fadeOut(tween(500))) { Splash() }
        }
    }

    if (vm.showOutput) ModalBottomSheet({ vm.showOutput = false }, containerColor = LineColors.Midnight) {
        Column(Modifier.fillMaxWidth().heightIn(min = 320.dp, max = 560.dp).padding(horizontal = 12.dp)) {
            ScrollableTabRow(vm.outTab, containerColor = Color.Transparent, contentColor = LineColors.Gold, edgePadding = 4.dp) {
                listOf("Output", "Input", "AI", "History", "Trace", "Console", "Help").forEachIndexed { i, t -> Tab(vm.outTab == i, { vm.outTab = i }, text = { Text(t) }) }
            }
            Box(Modifier.weight(1f).fillMaxWidth().padding(top = 8.dp)) {
                when (vm.outTab) {
                    0 -> SelectionContainer {
                        Column(Modifier.verticalScroll(rememberScrollState())) {
                            vm.output.lines().take(300).forEachIndexed { i, line ->
                                val st = remember(i, line) { MutableTransitionState(false).apply { targetState = true } }
                                AnimatedVisibility(st, enter = fadeIn(tween(150)) + slideInVertically(tween(150)) { it / 4 }) {
                                    Text(line, color = if (vm.isError) LineColors.Error else LineColors.Ok,
                                        fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                                }
                            }
                            if (vm.isError) {
                                val tip = vm.explainError()
                                if (tip.isNotBlank()) Text("💡 $tip", Modifier.padding(top = 10.dp), color = LineColors.Gold, fontSize = 13.sp)
                            }
                            vm.plots.forEach { p ->
                                val bmp = remember(p, vm.status) { BitmapFactory.decodeFile(p)?.asImageBitmap() }
                                if (bmp != null) Image(bmp, "plot", Modifier.fillMaxWidth().padding(top = 8.dp), contentScale = ContentScale.FillWidth)
                            }
                        }
                    }
                    1 -> OutlinedTextField(vm.stdin, { vm.stdin = it }, Modifier.fillMaxSize(),
                        label = { Text("stdin (one line per input() call)") })
                    5 -> Column {
                        var cmd by remember { mutableStateOf("") }
                        SelectionContainer(Modifier.weight(1f)) { Text(vm.replLog, Modifier.verticalScroll(rememberScrollState()),
                            color = LineColors.Ok, fontFamily = FontFamily.Monospace, fontSize = 12.sp) }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(cmd, { cmd = it }, Modifier.weight(1f), singleLine = true, label = { Text(">>>") })
                            TextButton({ if (cmd.isNotBlank()) { vm.replRun(cmd); cmd = "" } }) { Text("Send") }
                        }
                    }
                    6 -> Column(Modifier.verticalScroll(rememberScrollState())) {
                        Text(com.line.ide.ui.CHEATSHEET, color = LineColors.Ivory, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                    }
                    4 -> SelectionContainer { Column(Modifier.verticalScroll(rememberScrollState())) {
                        Text(vm.trace.ifBlank { "Use the menu: Run with trace, to see every executed line with its variables." },
                            color = LineColors.Ivory, fontFamily = FontFamily.Monospace, fontSize = 12.sp) } }
                    3 -> Column(Modifier.verticalScroll(rememberScrollState())) {
                        if (vm.history.isEmpty()) Text("No runs yet.", color = LineColors.Muted)
                        vm.history.forEach { Text(it, color = LineColors.Ivory, fontSize = 12.sp, fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(bottom = 8.dp)) }
                    }
                    else -> Column {
                        OutlinedTextField(vm.aiQuestion, { vm.aiQuestion = it }, Modifier.fillMaxWidth(), singleLine = true,
                            label = { Text("Ask about your code (then tap Ask)") })
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AiAction.entries.forEach { a -> AssistChip({ vm.ask(a) }, { Text(a.label) }, enabled = !vm.aiBusy) }
                        }
                        SelectionContainer { Text(vm.aiText.ifBlank { "Ask Line AI to explain, fix or improve your code." },
                            Modifier.verticalScroll(rememberScrollState()).padding(top = 8.dp), color = LineColors.Ivory, fontSize = 13.sp) }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }

    if (showFiles) ModalBottomSheet({ showFiles = false }, containerColor = LineColors.Midnight) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Workspace", Modifier.weight(1f), color = LineColors.Gold, fontSize = 18.sp)
                TextButton({ picker.launch("*/*") }) { Text("Import") }
                TextButton({ showNew = true }) { Icon(Icons.Default.Add, null); Text("New") }
            }
            vm.files.forEach { f ->
                Row(Modifier.fillMaxWidth().clickable { vm.open(f); showFiles = false }.padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(f, Modifier.weight(1f), color = if (f == vm.current) LineColors.Gold else LineColors.Ivory)
                    IconButton({ vm.delete(f) }) { Icon(Icons.Default.Delete, null, tint = LineColors.Muted) }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    if (showNew) {
        var name by remember { mutableStateOf("") }
        var tpl by remember { mutableStateOf("Hello") }
        AlertDialog({ showNew = false },
            confirmButton = { TextButton({ vm.newFile(name, tpl); showNew = false; showFiles = false }) { Text("Create") } },
            title = { Text("New file") },
            text = { Column(Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(name, { name = it }, label = { Text("name.py") }, singleLine = true)
                TEMPLATES.keys.forEach { k -> Row(Modifier.clickable { tpl = k }, verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(tpl == k, { tpl = k }); Text(k) } }
            } })
    }

    vm.inputPrompt?.let { prompt ->
        var text by remember(vm.inputSeq) { mutableStateOf("") }
        AlertDialog({ vm.submitInput("") },
            confirmButton = { TextButton({ vm.submitInput(text) }) { Text("Enter") } },
            title = { Text("Input") },
            text = { Column {
                Text(prompt, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                OutlinedTextField(text, { text = it }, singleLine = true, label = { Text("Type here, then Enter") })
            } })
    }

    if (showPip) {
        var pkg by remember { mutableStateOf("") }
        AlertDialog({ showPip = false },
            confirmButton = { TextButton({ vm.installPackage(pkg) }, enabled = !vm.pipBusy) { Text(if (vm.pipBusy) "Installing…" else "Install") } },
            dismissButton = { TextButton({ showPip = false }) { Text("Close") } },
            title = { Text("Install package") },
            text = { Column {
                OutlinedTextField(pkg, { pkg = it }, singleLine = true, label = { Text("PyPI name, e.g. colorama") })
                Text(vm.pipLog, Modifier.padding(top = 8.dp), fontSize = 12.sp, color = LineColors.Muted)
            } })
    }

    if (showLessons) AlertDialog({ showLessons = false },
        confirmButton = { TextButton({ showLessons = false }) { Text("Close") } },
        title = { Text("Learn Python") },
        text = { Column(Modifier.verticalScroll(rememberScrollState())) {
            com.line.ide.ui.LESSONS.forEachIndexed { i, l ->
                Text(l.title, Modifier.fillMaxWidth().clickable { lessonIdx = i; showLessons = false }.padding(vertical = 12.dp), color = LineColors.Ivory)
            }
        } })

    if (lessonIdx >= 0) {
        val l = com.line.ide.ui.LESSONS[lessonIdx]
        var ans by remember(lessonIdx) { mutableStateOf("") }
        var result by remember(lessonIdx) { mutableStateOf("") }
        AlertDialog({ lessonIdx = -1 },
            confirmButton = { TextButton({ vm.importCode("lesson_${lessonIdx + 1}.py", l.code); lessonIdx = -1 }) { Text("Try the code") } },
            dismissButton = { Row {
                TextButton({ showLessons = true; lessonIdx = -1 }) { Text("All lessons") }
                TextButton({ lessonIdx = -1 }) { Text("Close") }
            } },
            title = { Text(l.title) },
            text = { Column(Modifier.verticalScroll(rememberScrollState())) {
                Text(l.body, fontSize = 14.sp)
                Text(l.code, Modifier.padding(vertical = 8.dp), fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = LineColors.Gold)
                Text("Quiz: ${l.question}", fontSize = 14.sp)
                OutlinedTextField(ans, { ans = it }, singleLine = true, label = { Text("Your answer") })
                TextButton({ result = if (ans.trim().equals(l.answer, ignoreCase = true)) "✓ Correct! 🏅" else "✗ Try again" }) { Text("Check") }
                Text(result, color = if (result.startsWith("✓")) LineColors.Ok else LineColors.Error)
            } })
    }

    if (tour >= 0) {
        val pages = listOf(
            "Welcome to Line" to "Write Python, tap Run, and read the output in the Console panel.",
            "Your tools" to "Open the menu at the top right for editing tools, debugging, snippets, lint and more.",
            "Make it yours" to "Menu, then Themes, has 134 animated themes. Theme sounds can be switched on or off in Settings.")
        AlertDialog({ vm.prefs.onboarded = true; tour = -1 },
            confirmButton = { TextButton({
                if (tour < pages.size - 1) tour += 1 else { vm.prefs.onboarded = true; tour = -1 }
            }) { Text(if (tour < pages.size - 1) "Next" else "Start") } },
            title = { Text(pages[tour].first) }, text = { Text(pages[tour].second) })
    }

    challengeText?.let { ch ->
        AlertDialog({ challengeText = null },
            confirmButton = { TextButton({ vm.importCode("challenge.py", "# Challenge: $ch\n\n"); challengeText = null }) { Text("Start in editor") } },
            dismissButton = { TextButton({ challengeText = null }) { Text("Later") } },
            title = { Text("Daily challenge") }, text = { Text(ch) })
    }

    if (showStats) InfoDialog("Code statistics", vm.stats()) { showStats = false }
    if (showLint) InfoDialog("Lint (PEP 8 quick check)", vm.lint().ifEmpty { listOf("No issues found ✓") }.joinToString("\n")) { showLint = false }
    tipText?.let { InfoDialog("Python tip", it) { tipText = null } }

    if (showImport) AlertDialog({ showImport = false },
        confirmButton = { TextButton({ showImport = false }) { Text("Close") } },
        title = { Text("Add import") },
        text = { Column(Modifier.verticalScroll(rememberScrollState())) {
            listOf("math", "random", "os", "sys", "json", "re", "time", "datetime", "collections", "itertools", "functools",
                "statistics", "string", "csv", "pathlib", "typing", "numpy as np", "pandas as pd", "matplotlib.pyplot as plt").forEach { n ->
                Text("import $n", Modifier.fillMaxWidth().clickable { vm.addImport(n); showImport = false }.padding(vertical = 10.dp),
                    fontFamily = FontFamily.Monospace, fontSize = 13.sp)
            }
        } })

    if (showSnips) {
        val snips = remember(snipTick, showSnips) { vm.mySnippets() }
        AlertDialog({ showSnips = false },
            confirmButton = { TextButton({ showSnips = false }) { Text("Close") } },
            title = { Text("My snippets") },
            text = { Column(Modifier.verticalScroll(rememberScrollState())) {
                if (snips.isEmpty()) Text("Nothing saved yet. Select code, then menu: Save selection as snippet.", color = LineColors.Muted, fontSize = 13.sp)
                snips.forEachIndexed { i, sn ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(sn.lines().first().take(40), Modifier.weight(1f).clickable { vm.insert(sn); showSnips = false }.padding(vertical = 10.dp),
                            fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                        IconButton({ vm.deleteSnippet(i); snipTick += 1 }) { Icon(Icons.Default.Delete, null, tint = LineColors.Muted) }
                    }
                }
            } })
    }

    if (showRename) {
        var nn by remember { mutableStateOf(vm.current.removeSuffix(".py")) }
        AlertDialog({ showRename = false },
            confirmButton = { TextButton({ vm.rename(vm.current, nn); showRename = false }) { Text("Rename") } },
            title = { Text("Rename file") },
            text = { OutlinedTextField(nn, { nn = it }, singleLine = true, label = { Text("new name") }) })
    }

    if (showOutline) AlertDialog({ showOutline = false },
        confirmButton = { TextButton({ showOutline = false }) { Text("Close") } },
        title = { Text("Outline") },
        text = { Column(Modifier.verticalScroll(rememberScrollState())) {
            val items = vm.outline()
            if (items.isEmpty()) Text("No functions or classes yet.", color = LineColors.Muted)
            items.forEach { (ln, name) ->
                Text("$ln   $name", Modifier.fillMaxWidth().clickable { vm.jumpTo(ln); showOutline = false }.padding(vertical = 10.dp),
                    color = LineColors.Ivory, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
            }
        } })

    if (showThemes) AlertDialog({ showThemes = false },
        confirmButton = { TextButton({ showThemes = false }) { Text("Done") } },
        title = { Text("Themes") },
        text = { Column {
            Row(Modifier.horizontalScroll(rememberScrollState())) {
                listOf("All", "★ Favourites", "Space", "Nature", "Weather", "Neon & Fun").forEach { c ->
                    FilterChip(themeCat == c, { themeCat = c }, { Text(c, fontSize = 12.sp) }, Modifier.padding(end = 6.dp))
                }
            }
            Column(Modifier.heightIn(max = 380.dp).verticalScroll(rememberScrollState())) {
                PALETTES.withIndex().filter { (_, p) ->
                    themeCat == "All" || (themeCat == "★ Favourites" && p.name in vm.favs) || themeCat == p.category()
                }.forEach { (i, p) ->
                    Row(Modifier.fillMaxWidth().clickable { vm.setTheme(i) }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        listOf(p.obsidian, p.glow, p.gold, p.violet).forEach { c ->
                            Box(Modifier.size(14.dp).clip(RoundedCornerShape(7.dp)).background(c))
                            Spacer(Modifier.width(3.dp))
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(p.name, Modifier.weight(1f), color = if (p == LineColors.p) LineColors.Gold else LineColors.Ivory)
                        Text(if (p.name in vm.favs) "★" else "☆", Modifier.clickable { vm.toggleFav(p.name) }.padding(8.dp), color = LineColors.Gold)
                    }
                }
            }
        } })

    if (showSettings) {
        var key by remember { mutableStateOf(vm.prefs.apiKey) }
        var model by remember { mutableStateOf(vm.prefs.model) }
        var to by remember { mutableStateOf(vm.prefs.timeout.toString()) }
        var fs by remember { mutableStateOf(vm.prefs.fontSize.toString()) }
        AlertDialog({ showSettings = false },
            confirmButton = { TextButton({
                vm.prefs.apiKey = key; vm.prefs.model = model; vm.prefs.timeout = to.toIntOrNull() ?: 30
                vm.prefs.fontSize = fs.toIntOrNull() ?: 14; vm.fontSize = vm.prefs.fontSize; showSettings = false
            }) { Text("Save") } },
            title = { Text("Settings") },
            text = { Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(key, { key = it }, label = { Text("OpenRouter API key") }, singleLine = true)
                OutlinedTextField(model, { model = it }, label = { Text("AI model") }, singleLine = true)
                OutlinedTextField(to, { to = it.filter(Char::isDigit) }, label = { Text("Run timeout (seconds)") }, singleLine = true)
                OutlinedTextField(fs, { fs = it.filter(Char::isDigit) }, label = { Text("Editor font size (10-28)") }, singleLine = true)
                TextButton({ showSettings = false; showThemes = true }) { Text("Choose theme…") }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Animated background", Modifier.weight(1f))
                    Switch(LineColors.animate, { LineColors.animate = it; vm.prefs.animate = it })
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Theme sounds", Modifier.weight(1f))
                    Switch(vm.sound, { vm.setSound(it) })
                }
            } })
    }
}
