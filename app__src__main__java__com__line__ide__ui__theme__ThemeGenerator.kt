package com.line.ide.ui.theme

import androidx.compose.ui.graphics.Color

private val HUES = listOf("Ruby" to 0f, "Amber" to 25f, "Gold" to 45f, "Lime" to 95f, "Emerald" to 150f,
    "Teal" to 185f, "Sapphire" to 210f, "Indigo" to 255f, "Violet" to 285f, "Rose" to 330f)

private val EFFECTS = listOf("🎆 Fireworks" to Fx.FIREWORKS, "🎊 Confetti" to Fx.CONFETTI, "🫧 Bubbles" to Fx.BUBBLES,
    "💖 Hearts" to Fx.HEARTS, "🎯 Rings" to Fx.RINGS, "☄ Comet" to Fx.COMET, "🌀 Spiral" to Fx.SPIRAL,
    "💎 Diamonds" to Fx.DIAMONDS, "🎀 Ribbons" to Fx.RIBBONS, "✨ Stardust" to Fx.STARDUST)

/** 10 animation types x 10 colour moods = 100 themes. */
val GENERATED_PALETTES: List<Palette> = EFFECTS.flatMap { (name, fx) ->
    HUES.map { (hueName, h) ->
        Palette("$name · $hueName", true,
            Color.hsl(h, 0.5f, 0.05f), Color.hsl(h, 0.45f, 0.09f), Color.hsl(h, 0.8f, 0.62f), Color.hsl(h, 0.4f, 0.95f),
            Color.hsl(h, 0.2f, 0.55f), Color(0x0DFFFFFF), Color.hsl(h, 0.6f, 0.18f), Color.hsl((h + 40f) % 360f, 0.8f, 0.70f), fx)
    }
}
