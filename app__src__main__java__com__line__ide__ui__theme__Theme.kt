package com.line.ide.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

data class Palette(val name: String, val dark: Boolean, val obsidian: Color, val midnight: Color,
                   val gold: Color, val ivory: Color, val muted: Color)

val PALETTES = listOf(
    Palette("Obsidian Gold", true, Color(0xFF0B0F1A), Color(0xFF141B2D), Color(0xFFF5C04A), Color(0xFFFFF8E7), Color(0xFF6C7691)),
    Palette("Dracula", true, Color(0xFF282A36), Color(0xFF343746), Color(0xFFBD93F9), Color(0xFFF8F8F2), Color(0xFF6272A4)),
    Palette("Ivory Light", false, Color(0xFFFAF7F0), Color(0xFFEDE6D6), Color(0xFF9A6B00), Color(0xFF1B1F2A), Color(0xFF7A7F8E))
)

object LineColors {
    var p by mutableStateOf(PALETTES[0])
    val Obsidian get() = p.obsidian
    val Midnight get() = p.midnight
    val Gold get() = p.gold
    val Ivory get() = p.ivory
    val Muted get() = p.muted
    val Error = Color(0xFFFF6B6B)
    val Ok get() = if (p.dark) Color(0xFF6BE3A0) else Color(0xFF1E8E4E)
}

@Composable
fun LineTheme(content: @Composable () -> Unit) {
    val c = LineColors
    MaterialTheme(
        colorScheme = if (c.p.dark)
            darkColorScheme(primary = c.Gold, onPrimary = c.Obsidian, background = c.Obsidian, onBackground = c.Ivory,
                surface = c.Midnight, onSurface = c.Ivory, error = c.Error)
        else lightColorScheme(primary = c.Gold, onPrimary = Color.White, background = c.Obsidian, onBackground = c.Ivory,
                surface = c.Midnight, onSurface = c.Ivory, error = c.Error),
        content = content
    )
}
