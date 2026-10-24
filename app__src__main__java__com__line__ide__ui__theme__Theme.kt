package com.line.ide.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

data class Palette(
    val name: String, val dark: Boolean, val obsidian: Color, val midnight: Color, val gold: Color,
    val ivory: Color, val muted: Color, val glass: Color, val glow: Color, val violet: Color, val fx: Fx = Fx.AURORA
)

val PALETTES = listOf(
    Palette("Aurora Gold", true, Color(0xFF0B0B12), Color(0xFF15101F), Color(0xFFD4AF37), Color(0xFFE8E6F0), Color(0xFF7E7A94), Color(0x0DFFFFFF), Color(0xFF1A1033), Color(0xFF8B5CF6), Fx.AURORA),
    Palette("🔥 Fire", true, Color(0xFF120806), Color(0xFF1E0F0A), Color(0xFFFF8A3D), Color(0xFFFFEFE0), Color(0xFF9C6B55), Color(0x0DFFFFFF), Color(0xFF5A1A08), Color(0xFFFFD36B), Fx.FIRE),
    Palette("💧 Water", true, Color(0xFF04111F), Color(0xFF08203A), Color(0xFF38BDF8), Color(0xFFE6F6FF), Color(0xFF6F94B0), Color(0x0DFFFFFF), Color(0xFF0A3A66), Color(0xFF7DD3FC), Fx.WATER),
    Palette("🌍 Earth", true, Color(0xFF120E08), Color(0xFF1F1810), Color(0xFFC8A15A), Color(0xFFF3EAD8), Color(0xFF9B8B70), Color(0x0DFFFFFF), Color(0xFF3B2A14), Color(0xFF8DA05A), Fx.EARTH),
    Palette("🌬 Air", true, Color(0xFF0E1620), Color(0xFF17232F), Color(0xFFCFE8FF), Color(0xFFF5FAFF), Color(0xFF8AA0B5), Color(0x0DFFFFFF), Color(0xFF24405A), Color(0xFFA5B4FC), Fx.AIR),
    Palette("⚡ Lightning", true, Color(0xFF07070F), Color(0xFF12121F), Color(0xFFFACC15), Color(0xFFF4F4FF), Color(0xFF7A7A99), Color(0x0DFFFFFF), Color(0xFF1B1B4A), Color(0xFF93C5FD), Fx.LIGHTNING),
    Palette("❄ Ice", true, Color(0xFF06121A), Color(0xFF0C1F2C), Color(0xFF67E8F9), Color(0xFFECFEFF), Color(0xFF6B93A1), Color(0x0DFFFFFF), Color(0xFF0E3A4D), Color(0xFFBAE6FD), Fx.ICE),
    Palette("🌿 Forest", true, Color(0xFF06100A), Color(0xFF0D1E14), Color(0xFF86EFAC), Color(0xFFEAFBEF), Color(0xFF6B9B7C), Color(0x0DFFFFFF), Color(0xFF0F3A22), Color(0xFFBEF264), Fx.FOREST),
    Palette("🌌 Cosmos", true, Color(0xFF05030F), Color(0xFF0E0A22), Color(0xFFC4B5FD), Color(0xFFEEE9FF), Color(0xFF7F77AA), Color(0x0DFFFFFF), Color(0xFF1E1250), Color(0xFFF0ABFC), Fx.COSMOS),
    Palette("Dracula", true, Color(0xFF282A36), Color(0xFF343746), Color(0xFFBD93F9), Color(0xFFF8F8F2), Color(0xFF6272A4), Color(0x14FFFFFF), Color(0xFF44475A), Color(0xFFFF79C6), Fx.AURORA),
    Palette("Ivory Light", false, Color(0xFFFAF7F0), Color(0xFFEDE6D6), Color(0xFF9A6B00), Color(0xFF1B1F2A), Color(0xFF7A7F8E), Color(0x14000000), Color(0xFFFFFFFF), Color(0xFF6D3FD6), Fx.NONE)
)

object LineColors {
    var p by mutableStateOf(PALETTES[0])
    val Obsidian get() = p.obsidian
    val Midnight get() = p.midnight
    val Gold get() = p.gold
    val Ivory get() = p.ivory
    val Muted get() = p.muted
    val Glass get() = p.glass
    val Violet get() = p.violet
    val Error = Color(0xFFFF6B6B)
    val Ok get() = if (p.dark) Color(0xFF6BE3A0) else Color(0xFF1E8E4E)
}

@Composable
fun LineTheme(content: @Composable () -> Unit) {
    val c = LineColors
    MaterialTheme(
        colorScheme = if (c.p.dark)
            darkColorScheme(primary = c.Gold, onPrimary = Color(0xFF1A1405), background = c.Obsidian, onBackground = c.Ivory,
                surface = c.Midnight, onSurface = c.Ivory, error = c.Error, secondary = c.Violet)
        else lightColorScheme(primary = c.Gold, onPrimary = Color.White, background = c.Obsidian, onBackground = c.Ivory,
                surface = c.Midnight, onSurface = c.Ivory, error = c.Error, secondary = c.Violet),
        content = content
    )
}
