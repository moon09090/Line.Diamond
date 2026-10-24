package com.line.ide.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.line.ide.ui.SNIPPETS
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.line.ide.ui.theme.PALETTES
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import com.line.ide.ai.AiAction
import com.line.ide.ui.IdeViewModel
import com.line.ide.ui.TEMPLATES
import com.line.ide.ui.editor.CodeEditor
import com.line.ide.ui.theme.LineColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdeScreen(vm: IdeViewModel) {
    var tab by remember { mutableIntStateOf(0) }
    var showFiles by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showNew by remember { mutableStateOf(false) }
    val ctx = LocalContext.current
    var menu by remember { mutableStateOf(false) }
    val clip = LocalClipboardManager.current
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { vm.importCode(it.lastPathSegment?.substringAfterLast('/') ?: "imported.py",
            ctx.contentResolver.openInputStream(it)?.bufferedReader()?.readText() ?: "") }
    }

    Scaffold(
        containerColor = LineColors.Obsidian,
        topBar = {
            TopAppBar(
                title = { Column {
                    Text(vm.current, fontSize = 16.sp, color = LineColors.Ivory)
                    Text(vm.status, fontSize = 11.sp, color = if (vm.isError) LineColors.Error else LineColors.Muted)
                } },
                navigationIcon = { IconButton({ showFiles = true }) { Icon(Icons.Default.Folder, null, tint = LineColors.Gold) } },
                actions = {
                    Box { IconButton({ menu = true }) { Icon(Icons.Default.MoreVert, "More", tint = LineColors.Muted) }
                        DropdownMenu(menu, { menu = false }) {
                            listOf<Pair<String, () -> Unit>>("Undo" to { vm.undo() }, "Redo" to { vm.redo() },
                                "Find & replace" to { vm.showFind = !vm.showFind }, "Tidy code" to { vm.tidy() },
                                "Clear output" to { vm.output = ""; vm.plots.clear() },
                                "Toggle comment" to { vm.toggleComment() },
                                "Copy output" to { clip.setText(AnnotatedString(vm.output)) },
                                "Share code" to { ctx.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain")
                                    .putExtra(Intent.EXTRA_TEXT, vm.editor.text), "Share code")) }
                            ).forEach { (l, a) -> DropdownMenuItem({ Text(l) }, { menu = false; a() }) }
                        } }
                    IconButton({ showSettings = true }) { Icon(Icons.Default.Settings, null, tint = LineColors.Muted) }
                    FilledIconButton({ vm.run() }, enabled = !vm.running) { Icon(Icons.Default.PlayArrow, "Run") }
                    Spacer(Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LineColors.Midnight)
            )
        }
    ) { pad ->
        Column(Modifier.padding(pad).fillMaxSize().imePadding()) {
            Row(Modifier.fillMaxWidth().background(LineColors.Midnight).horizontalScroll(rememberScrollState()).padding(horizontal = 4.dp)) {
                vm.tabs.forEach { t ->
                    InputChip(t == vm.current, { vm.open(t) }, { Text(t, fontSize = 12.sp) }, Modifier.padding(2.dp),
                        trailingIcon = { Icon(Icons.Default.Close, "close", Modifier.size(16.dp).clickable { vm.closeTab(t) }) })
                }
            }
            if (vm.showFind) Row(Modifier.fillMaxWidth().background(LineColors.Midnight).padding(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedTextField(vm.findText, { vm.findText = it }, Modifier.weight(1f), singleLine = true, label = { Text("Find") })
                OutlinedTextField(vm.replaceText, { vm.replaceText = it }, Modifier.weight(1f), singleLine = true, label = { Text("Replace") })
                Column { TextButton({ vm.findNext() }) { Text("Next") }; TextButton({ vm.replaceAll() }) { Text("All") } }
            }
            CodeEditor(vm.editor, vm.errorLine, vm.fontSize, vm::onEdit, Modifier.weight(1f).fillMaxWidth())
            Row(Modifier.fillMaxWidth().background(LineColors.Midnight).horizontalScroll(rememberScrollState()).padding(4.dp)) {
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
            TabRow(tab, containerColor = LineColors.Midnight, contentColor = LineColors.Gold) {
                listOf("Output", "Input", "AI", "History").forEachIndexed { i, t -> Tab(tab == i, { tab = i }, text = { Text(t) }) }
            }
            Box(Modifier.height(220.dp).fillMaxWidth().background(LineColors.Obsidian).padding(10.dp)) {
                when (tab) {
                    0 -> Column(Modifier.verticalScroll(rememberScrollState())) {
                        SelectionContainer { Text(vm.output, color = if (vm.isError) LineColors.Error else LineColors.Ok,
                            fontFamily = FontFamily.Monospace, fontSize = 13.sp) }
                        vm.plots.forEach { p ->
                            val bmp = remember(p, vm.status) { BitmapFactory.decodeFile(p)?.asImageBitmap() }
                            if (bmp != null) Image(bmp, "plot", Modifier.fillMaxWidth().padding(top = 8.dp), contentScale = ContentScale.FillWidth)
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
                            AiAction.entries.forEach { a ->
                                AssistChip({ vm.ask(a) }, { Text(a.label) }, enabled = !vm.aiBusy)
                            }
                        }
                        SelectionContainer { Text(vm.aiText.ifBlank { "Ask Line AI to explain, fix or improve your code." },
                            Modifier.verticalScroll(rememberScrollState()).padding(top = 8.dp), color = LineColors.Ivory, fontSize = 13.sp) }
                    }
                }
            }
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
            text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(key, { key = it }, label = { Text("OpenRouter API key") }, singleLine = true)
                OutlinedTextField(model, { model = it }, label = { Text("AI model") }, singleLine = true)
                OutlinedTextField(to, { to = it.filter(Char::isDigit) }, label = { Text("Run timeout (seconds)") }, singleLine = true)
                OutlinedTextField(fs, { fs = it.filter(Char::isDigit) }, label = { Text("Editor font size (10-28)") }, singleLine = true)
                Row { PALETTES.forEachIndexed { i, p -> TextButton({ vm.setTheme(i) }) { Text(p.name, fontSize = 11.sp) } } }
            } })
    }
}
