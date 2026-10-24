package com.line.ide.ui.screens

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
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
    .border(1.dp, LineColors.Gold.copy(alpha = 0.25f), RoundedCornerShape(14.dp))

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
    val ctx = LocalContext.current
    val clip = LocalClipboardManager.current
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { vm.importCode(it.lastPathSegment?.substringAfterLast('/') ?: "imported.py",
            ctx.contentResolver.openInputStream(it)?.bufferedReader()?.readText() ?: "") }
    }

    ElementBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Column {
                        Text(vm.current, fontSize = 16.sp, color = LineColors.Ivory, fontFamily = FontFamily.Monospace)
                        Text(vm.status, fontSize = 11.sp, color = if (vm.isError) LineColors.Error else LineColors.Muted)
                    } },
                    navigationIcon = { IconButton({ showFiles = true }) { Icon(Icons.Default.Folder, null, tint = LineColors.Gold) } },
                    actions = {
                        TextButton({ vm.showOutput = true }) { Text("Console", color = LineColors.Violet) }
                        Box { IconButton({ menu = true }) { Icon(Icons.Default.MoreVert, "More", tint = LineColors.Muted) }
                            DropdownMenu(menu, { menu = false }) {
                                listOf<Pair<String, () -> Unit>>("Undo" to { vm.undo() }, "Redo" to { vm.redo() },
                                    "Find & replace" to { vm.showFind = !vm.showFind }, "Tidy code" to { vm.tidy() },
                                    "Toggle comment" to { vm.toggleComment() },
                                    "Clear output" to { vm.output = ""; vm.plots.clear() },
                                    "Copy output" to { clip.setText(AnnotatedString(vm.output)) },
                                    "Themes" to { showThemes = true },
                                    "Settings" to { showSettings = true },
                                    "Share code" to { ctx.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain")
                                        .putExtra(Intent.EXTRA_TEXT, vm.editor.text), "Share code")) }
                                ).forEach { (l, a) -> DropdownMenuItem({ Text(l) }, { menu = false; a() }) }
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
                CodeEditor(vm.editor, vm.errorLine, vm.fontSize, vm::onEdit,
                    Modifier.weight(1f).fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp).glass())
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(4.dp)) {
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
    }

    if (vm.showOutput) ModalBottomSheet({ vm.showOutput = false }, containerColor = LineColors.Midnight) {
        Column(Modifier.fillMaxWidth().heightIn(min = 320.dp, max = 560.dp).padding(horizontal = 12.dp)) {
            TabRow(vm.outTab, containerColor = Color.Transparent, contentColor = LineColors.Gold) {
                listOf("Output", "Input", "AI", "History").forEachIndexed { i, t -> Tab(vm.outTab == i, { vm.outTab = i }, text = { Text(t) }) }
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
                            vm.plots.forEach { p ->
                                val bmp = remember(p, vm.status) { BitmapFactory.decodeFile(p)?.asImageBitmap() }
                                if (bmp != null) Image(bmp, "plot", Modifier.fillMaxWidth().padding(top = 8.dp), contentScale = ContentScale.FillWidth)
                            }
                        }
                    }
                    1 -> OutlinedTextField(vm.stdin, { vm.stdin = it }, Modifier.fillMaxSize(),
                        label = { Text("stdin (one line per input() call)") })
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

    if (showThemes) AlertDialog({ showThemes = false },
        confirmButton = { TextButton({ showThemes = false }) { Text("Done") } },
        title = { Text("Themes") },
        text = { Column(Modifier.verticalScroll(rememberScrollState())) {
            PALETTES.forEachIndexed { i, p ->
                Row(Modifier.fillMaxWidth().clickable { vm.setTheme(i) }.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(22.dp).clip(RoundedCornerShape(11.dp)).background(Brush.linearGradient(listOf(p.glow, p.gold))))
                    Spacer(Modifier.width(12.dp))
                    Text(p.name, color = if (p == LineColors.p) LineColors.Gold else LineColors.Ivory)
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
            } })
    }
}
