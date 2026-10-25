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
    Palette("🌸 Sakura", true, Color(0xFF140A12), Color(0xFF22101C), Color(0xFFFF9EC4), Color(0xFFFFEAF2), Color(0xFFA17A8C), Color(0x0DFFFFFF), Color(0xFF4A1633), Color(0xFFFFC2DD), Fx.SAKURA),
    Palette("💻 Matrix", true, Color(0xFF010A03), Color(0xFF06160A), Color(0xFF00FF66), Color(0xFFD6FFE3), Color(0xFF3F8F5F), Color(0x0DFFFFFF), Color(0xFF03361A), Color(0xFF7CFFB2), Fx.MATRIX),
    Palette("🌆 Neon", true, Color(0xFF0A0420), Color(0xFF150838), Color(0xFFFF2BD6), Color(0xFFFDEBFF), Color(0xFF8A6FB5), Color(0x0DFFFFFF), Color(0xFF3B0A66), Color(0xFF22D3EE), Fx.NEON),
    Palette("🌋 Lava", true, Color(0xFF100503), Color(0xFF1C0A06), Color(0xFFFF5A1F), Color(0xFFFFE9DD), Color(0xFFA0603F), Color(0x0DFFFFFF), Color(0xFF6B1505), Color(0xFFFFB020), Fx.LAVA),
    Palette("🌈 Prism", true, Color(0xFF07070E), Color(0xFF11111F), Color(0xFF22D3EE), Color(0xFFF5F5FF), Color(0xFF7F83A8), Color(0x0DFFFFFF), Color(0xFF1A1A3A), Color(0xFFF472B6), Fx.PRISM),
    Palette("🌼 Flowers", true, Color(0xFF130A14), Color(0xFF20112A), Color(0xFFFF8FB8), Color(0xFFFFF0F6), Color(0xFFA8809A), Color(0x0DFFFFFF), Color(0xFF4A1A3F), Color(0xFFFFD166), Fx.FLOWERS),
    Palette("🍃 Leaf", true, Color(0xFF07120A), Color(0xFF0E2014), Color(0xFF84CC16), Color(0xFFF0FFE6), Color(0xFF6E9A62), Color(0x0DFFFFFF), Color(0xFF17401F), Color(0xFFF59E0B), Fx.LEAF),
    Palette("💧 Droplets", true, Color(0xFF04101A), Color(0xFF0A1E30), Color(0xFF7DD3FC), Color(0xFFEAF8FF), Color(0xFF6A92AD), Color(0x0DFFFFFF), Color(0xFF0C3B5E), Color(0xFF5EEAD4), Fx.DROPLETS),
    Palette("☁ Sky", true, Color(0xFF0B1D3A), Color(0xFF15264A), Color(0xFFFFB86B), Color(0xFFFFF4E6), Color(0xFF8C9BC0), Color(0x0DFFFFFF), Color(0xFF3B2A6B), Color(0xFFFF9EC4), Fx.SKY),
    Palette("🌙 Moon", true, Color(0xFF05060F), Color(0xFF0D1124), Color(0xFFFFF3C4), Color(0xFFF5F3E8), Color(0xFF8A8DA8), Color(0x0DFFFFFF), Color(0xFF1B2250), Color(0xFFA5B4FC), Fx.MOON),
    Palette("🐱 Cartoon", true, Color(0xFF1A1030), Color(0xFF271848), Color(0xFFFFB86B), Color(0xFFFFF5E1), Color(0xFFB39DDB), Color(0x0DFFFFFF), Color(0xFF5B2A8A), Color(0xFFFF9EC4), Fx.CARTOON),
    Palette("🦋 Butterfly", true, Color(0xFF0A0818), Color(0xFF15112B), Color(0xFFF472B6), Color(0xFFFFEFF8), Color(0xFF9A8DBF), Color(0x0DFFFFFF), Color(0xFF34195E), Color(0xFF60A5FA), Fx.BUTTERFLY),
    Palette("🌸 Cherry Blossoms", true, Color(0xFF150A12), Color(0xFF24101C), Color(0xFFFFB3D1), Color(0xFFFFEEF5), Color(0xFFB08598), Color(0x0DFFFFFF), Color(0xFF5A1B3F), Color(0xFFFF7FAF), Fx.CHERRY),
    Palette("🪐 Solar System", true, Color(0xFF03030A), Color(0xFF0B0B1C), Color(0xFFFFC857), Color(0xFFF4F1E8), Color(0xFF8A87A3), Color(0x0DFFFFFF), Color(0xFF15163D), Color(0xFF4F9DFF), Fx.SOLAR),
    Palette("🌌 Galaxy", true, Color(0xFF04030D), Color(0xFF0C0A20), Color(0xFF9DB7FF), Color(0xFFECEFFF), Color(0xFF7D84B5), Color(0x0DFFFFFF), Color(0xFF241A5A), Color(0xFFFFE9B0), Fx.GALAXY),
    Palette("🌍 Planet Earth", true, Color(0xFF020712), Color(0xFF071426), Color(0xFF4FA8FF), Color(0xFFE6F3FF), Color(0xFF6F93B8), Color(0x0DFFFFFF), Color(0xFF0B2E66), Color(0xFF3DBE6B), Fx.EARTH_GLOBE),
    Palette("🌞 Sun Flare", true, Color(0xFF120703), Color(0xFF1F0F06), Color(0xFFFFB347), Color(0xFFFFF1DC), Color(0xFFB0825A), Color(0x0DFFFFFF), Color(0xFF5C2A06), Color(0xFFFFD27A), Fx.SUN),
    Palette("🌑 Eclipse", true, Color(0xFF05050A), Color(0xFF0F0F18), Color(0xFFFFF1C2), Color(0xFFF2F0E6), Color(0xFF8A8A99), Color(0x0DFFFFFF), Color(0xFF1A1A2E), Color(0xFFFFB347), Fx.ECLIPSE),
    Palette("🌠 Meteor Shower", true, Color(0xFF04060F), Color(0xFF0A1020), Color(0xFF9FD8FF), Color(0xFFEAF6FF), Color(0xFF7592B0), Color(0x0DFFFFFF), Color(0xFF0F2850), Color(0xFFC4B5FD), Fx.METEORS),
    Palette("🪐 Saturn", true, Color(0xFF0A0705), Color(0xFF17100A), Color(0xFFE9D8A6), Color(0xFFFFF6E0), Color(0xFFA89674), Color(0x0DFFFFFF), Color(0xFF3A2610), Color(0xFFF3D9A0), Fx.SATURN),
    Palette("🔭 Nebula", true, Color(0xFF07030F), Color(0xFF120A22), Color(0xFFFF7AC8), Color(0xFFFFEAF6), Color(0xFFA07CB5), Color(0x0DFFFFFF), Color(0xFF3A0F55), Color(0xFF22D3EE), Fx.NEBULA),
    Palette("🚀 Warp Speed", true, Color(0xFF02040C), Color(0xFF080E1E), Color(0xFF7DD3FC), Color(0xFFEAF8FF), Color(0xFF6A8CAD), Color(0x0DFFFFFF), Color(0xFF0C2A5E), Color(0xFFA5B4FC), Fx.WARP),
    Palette("Dracula", true, Color(0xFF282A36), Color(0xFF343746), Color(0xFFBD93F9), Color(0xFFF8F8F2), Color(0xFF6272A4), Color(0x14FFFFFF), Color(0xFF44475A), Color(0xFFFF79C6), Fx.AURORA),
    Palette("Ivory Light", false, Color(0xFFFAF7F0), Color(0xFFEDE6D6), Color(0xFF9A6B00), Color(0xFF1B1F2A), Color(0xFF7A7F8E), Color(0x14000000), Color(0xFFFFFFFF), Color(0xFF6D3FD6), Fx.NONE)
) + GENERATED_PALETTES

object LineColors {
    var p by mutableStateOf(PALETTES[0])
    var animate by mutableStateOf(true)
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

fun Palette.category(): String = when (fx) {
    Fx.COSMOS, Fx.SOLAR, Fx.GALAXY, Fx.EARTH_GLOBE, Fx.SUN, Fx.ECLIPSE, Fx.METEORS, Fx.SATURN, Fx.NEBULA,
    Fx.WARP, Fx.COMET, Fx.STARDUST, Fx.SPIRAL -> "Space"
    Fx.FIRE, Fx.WATER, Fx.EARTH, Fx.FOREST, Fx.FLOWERS, Fx.LEAF, Fx.DROPLETS, Fx.BUTTERFLY, Fx.SAKURA, Fx.CHERRY -> "Nature"
    Fx.AIR, Fx.LIGHTNING, Fx.ICE, Fx.SKY, Fx.MOON, Fx.AURORA, Fx.LAVA -> "Weather"
    else -> "Neon & Fun"
}
