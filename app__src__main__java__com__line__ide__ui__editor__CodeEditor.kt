package com.line.ide.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.line.ide.ui.theme.LineColors

private val mono = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp, lineHeight = 20.sp)

@Composable
fun CodeEditor(value: TextFieldValue, errorLine: Int, fontSize: Int, breakpoints: Set<Int>, onChange: (TextFieldValue) -> Unit, modifier: Modifier = Modifier) {
    val m = mono.copy(fontSize = fontSize.sp, lineHeight = (fontSize + 6).sp)
    val lines = value.text.count { it == '\n' } + 1
    Row(modifier.verticalScroll(rememberScrollState())) {
        Text(
            (1..lines).joinToString("\n") { if (it == errorLine) "●$it" else if (it in breakpoints) "◆$it" else "$it" },
            style = m.copy(color = if (errorLine > 0) LineColors.Error else LineColors.Muted, textAlign = TextAlign.End),
            modifier = Modifier.background(LineColors.Glass).padding(horizontal = 8.dp, vertical = 8.dp).widthIn(min = 36.dp)
        )
        BasicTextField(
            value = value, onValueChange = onChange,
            textStyle = m.copy(color = LineColors.Ivory),
            cursorBrush = SolidColor(LineColors.Gold),
            visualTransformation = SyntaxHighlighter.transformation,
            modifier = Modifier.weight(1f).horizontalScroll(rememberScrollState()).padding(8.dp)
        )
    }
}
