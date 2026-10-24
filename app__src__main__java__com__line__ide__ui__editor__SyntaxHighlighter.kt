package com.line.ide.ui.editor

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

object SyntaxHighlighter {
    val keywords = ("False None True and as assert async await break class continue def del elif else except " +
        "finally for from global if import in is lambda nonlocal not or pass raise return try while with yield match case").split(" ").toSet()
    val builtins = "print len range int str float list dict set tuple input open sum min max abs sorted enumerate zip map filter type isinstance".split(" ").toSet()

    private val rx = Regex(
        "(?<c>#[^\\n]*)|(?<s>\"\"\"[\\s\\S]*?(\"\"\"|\\z)|'''[\\s\\S]*?('''|\\z)|\"(\\\\.|[^\"\\\\\\n])*\"?|'(\\\\.|[^'\\\\\\n])*'?)" +
            "|(?<d>@\\w+)|(?<n>\\b\\d+(\\.\\d+)?\\b)|(?<w>\\b[A-Za-z_]\\w*\\b)"
    )

    fun highlight(src: String): AnnotatedString = buildAnnotatedString {
        append(src)
        for (m in rx.findAll(src)) {
            val g = m.groups as MatchNamedGroupCollection
            val style = when {
                g["c"] != null -> SpanStyle(color = Color(0xFF6C7691))
                g["s"] != null -> SpanStyle(color = Color(0xFF6BE3A0))
                g["d"] != null -> SpanStyle(color = Color(0xFFC792EA))
                g["n"] != null -> SpanStyle(color = Color(0xFFF78C6C))
                m.value in keywords -> SpanStyle(color = Color(0xFFF5C04A), fontWeight = FontWeight.Bold)
                m.value in builtins -> SpanStyle(color = Color(0xFF82AAFF))
                else -> null
            }
            if (style != null) addStyle(style, m.range.first, m.range.last + 1)
        }
    }

    val transformation = VisualTransformation { TransformedText(highlight(it.text), OffsetMapping.Identity) }
}
