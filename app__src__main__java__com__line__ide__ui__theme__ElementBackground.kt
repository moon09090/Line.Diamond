package com.line.ide.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

enum class Fx { AURORA, FIRE, WATER, EARTH, AIR, LIGHTNING, ICE, FOREST, COSMOS, SAKURA, MATRIX, NEON, LAVA, PRISM, FLOWERS, LEAF, DROPLETS, SKY, MOON, CARTOON, BUTTERFLY, CHERRY, SOLAR, GALAXY, EARTH_GLOBE, SUN, ECLIPSE, METEORS, SATURN, NEBULA, WARP, FIREWORKS, CONFETTI, BUBBLES, HEARTS, RINGS, COMET, SPIRAL, DIAMONDS, RIBBONS, STARDUST, NONE }

private const val TAU = 6.2832f

/** Deterministic pseudo-random number in 0..1 (so animations loop seamlessly). */
private fun rnd(i: Int, k: Int): Float {
    val x = sin(i * 12.9898 + k * 78.233) * 43758.5453
    return (x - floor(x)).toFloat()
}

@Composable
fun ElementBackground(content: @Composable () -> Unit) {
    val tr = rememberInfiniteTransition(label = "fx")
    val t by tr.animateFloat(0f, 1f, infiniteRepeatable(tween(30000, easing = LinearEasing)), label = "t")
    Box(Modifier.fillMaxSize().background(LineColors.Obsidian).drawBehind {
        val p = LineColors.p
        drawRect(Brush.radialGradient(listOf(p.glow, Color.Transparent),
            center = Offset(size.width * (0.5f + 0.35f * sin(t * TAU)), size.height * 0.22f), radius = size.width * 1.2f))
        drawRect(Brush.radialGradient(listOf(p.violet.copy(alpha = 0.16f), Color.Transparent),
            center = Offset(size.width * (0.5f - 0.35f * sin(t * TAU)), size.height * 0.85f), radius = size.width))
        when (if (LineColors.animate) p.fx else Fx.NONE) {
            Fx.FIRE -> fire(t)
            Fx.WATER -> water(t)
            Fx.EARTH -> earth(t)
            Fx.AIR -> air(t)
            Fx.LIGHTNING -> lightning(t)
            Fx.ICE -> ice(t)
            Fx.FOREST -> forest(t)
            Fx.COSMOS -> cosmos(t)
            Fx.SAKURA -> sakura(t)
            Fx.MATRIX -> matrix(t)
            Fx.NEON -> neon(t)
            Fx.LAVA -> lava(t)
            Fx.PRISM -> prism(t)
            Fx.FLOWERS -> flowers(t)
            Fx.LEAF -> leaves(t)
            Fx.DROPLETS -> droplets(t)
            Fx.SKY -> sky(t)
            Fx.MOON -> moon(t)
            Fx.CARTOON -> cartoon(t)
            Fx.BUTTERFLY -> butterflies(t)
            Fx.CHERRY -> cherry(t)
            Fx.SOLAR -> solar(t)
            Fx.GALAXY -> galaxy(t)
            Fx.EARTH_GLOBE -> earthGlobe(t)
            Fx.SUN -> sunFlare(t)
            Fx.ECLIPSE -> eclipse(t)
            Fx.METEORS -> meteors(t)
            Fx.SATURN -> saturn(t)
            Fx.NEBULA -> nebula(t)
            Fx.WARP -> warp(t)
            Fx.FIREWORKS -> fireworks(t)
            Fx.CONFETTI -> confetti(t)
            Fx.BUBBLES -> bubbles(t)
            Fx.HEARTS -> hearts(t)
            Fx.RINGS -> rings(t)
            Fx.COMET -> comet(t)
            Fx.SPIRAL -> spiral(t)
            Fx.DIAMONDS -> diamonds(t)
            Fx.RIBBONS -> ribbons(t)
            Fx.STARDUST -> stardust(t)
            else -> {}
        }
    }) { content() }
}

private fun DrawScope.fire(t: Float) {
    drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color(0x55FF4500)), startY = size.height * 0.5f, endY = size.height))
    for (i in 0 until 45) {
        val life = (t * (2 + (rnd(i, 1) * 4).toInt()) + rnd(i, 2)) % 1f
        val x = size.width * rnd(i, 3) + sin(life * 6f + i) * 14f
        val y = size.height * (1.02f - life * 0.9f)
        val r = (2f + rnd(i, 4) * 4f) * (1f - life * 0.6f)
        drawCircle(lerp(Color(0xFFFFD36B), Color(0xFFFF4500), life).copy(alpha = (1f - life) * 0.8f), r, Offset(x, y))
    }
}

private fun DrawScope.water(t: Float) {
    for (w in 0 until 4) {
        val path = Path()
        val base = size.height * (0.55f + w * 0.1f)
        val dir = if (w % 2 == 0) 1 else -1
        path.moveTo(0f, base)
        var x = 0f
        while (x <= size.width) {
            path.lineTo(x, base + (14f + w * 6f) * sin(x / (90f + w * 30f) + t * TAU * dir * (1 + w % 2)))
            x += 12f
        }
        drawPath(path, Color(0xFF38BDF8).copy(alpha = 0.2f - w * 0.035f), style = Stroke(width = 2.5f))
    }
    for (i in 0 until 22) {
        val life = (t * (2 + (rnd(i, 1) * 3).toInt()) + rnd(i, 2)) % 1f
        drawCircle(Color(0xFF7DD3FC).copy(alpha = (1f - life) * 0.5f), 2f + rnd(i, 4) * 5f,
            Offset(size.width * rnd(i, 3) + sin(life * 8f + i) * 10f, size.height * (1.02f - life * 1.05f)), style = Stroke(1.5f))
    }
}

private fun DrawScope.earth(t: Float) {
    drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color(0x443B2A14)), startY = size.height * 0.6f, endY = size.height))
    for (i in 0 until 55) {
        val life = (t * (1 + (rnd(i, 1) * 2).toInt()) + rnd(i, 2)) % 1f
        val c = lerp(Color(0xFFC8A15A), Color(0xFF8DA05A), rnd(i, 5))
        drawCircle(c.copy(alpha = 0.35f), 1.5f + rnd(i, 4) * 3.5f,
            Offset(size.width * rnd(i, 3) + sin(life * TAU * 2 + i) * 22f, size.height * (life * 1.1f - 0.05f)))
    }
}

private fun DrawScope.air(t: Float) {
    for (i in 0 until 24) {
        val life = (t * (3 + (rnd(i, 1) * 4).toInt()) + rnd(i, 2)) % 1f
        val x = (size.width + 300f) * life - 150f
        val y = size.height * rnd(i, 3) + sin(life * TAU) * 12f
        drawLine(Color.White.copy(alpha = 0.16f * sin(life * 3.1416f)), Offset(x, y),
            Offset(x + 60f + rnd(i, 4) * 100f, y + 6f), strokeWidth = 2f, cap = StrokeCap.Round)
    }
}

private fun DrawScope.lightning(t: Float) {
    for (i in 0 until 60) {
        val life = (t * (20 + (rnd(i, 1) * 10).toInt()) + rnd(i, 2)) % 1f
        val x = size.width * rnd(i, 3) + life * 40f
        val y = size.height * life
        drawLine(Color(0xFF93C5FD).copy(alpha = 0.25f), Offset(x, y), Offset(x + 6f, y + 22f), strokeWidth = 1.5f)
    }
    val cycle = t * 6f
    val idx = floor(cycle).toInt()
    val f = cycle - idx
    val flash = if (f < 0.12f) 1f - f / 0.12f else 0f
    if (flash > 0f && rnd(idx, 9) > 0.35f) {
        val path = Path()
        var x0 = size.width * (0.2f + 0.6f * rnd(idx, 5))
        path.moveTo(x0, 0f)
        for (s in 1..9) {
            x0 += (rnd(idx, 10 + s) - 0.5f) * 90f
            path.lineTo(x0, size.height * 0.8f * s / 9f)
        }
        drawRect(Color(0xFFBFDBFE).copy(alpha = flash * 0.12f))
        drawPath(path, Color.White.copy(alpha = flash), style = Stroke(width = 4f))
    }
}

private fun DrawScope.ice(t: Float) {
    for (i in 0 until 55) {
        val life = (t * (2 + (rnd(i, 1) * 3).toInt()) + rnd(i, 2)) % 1f
        drawCircle(Color(0xFFE0F7FF).copy(alpha = 0.7f), 1.5f + rnd(i, 4) * 3f,
            Offset(size.width * rnd(i, 3) + sin(life * TAU * 2 + i) * 18f, size.height * (life * 1.05f - 0.03f)))
    }
}

private fun DrawScope.forest(t: Float) {
    drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color(0x330F3A22)), startY = size.height * 0.6f, endY = size.height))
    for (i in 0 until 35) {
        val x = size.width * rnd(i, 3) + sin(t * TAU * (1 + i % 3) + i) * 30f
        val y = size.height * rnd(i, 4) + cos(t * TAU * (1 + i % 2) + i) * 30f
        val a = 0.2f + 0.6f * (0.5f + 0.5f * sin(t * TAU * (2 + i % 3) + i))
        drawCircle(Color(0xFFB9F27C).copy(alpha = a * 0.25f), 9f, Offset(x, y))
        drawCircle(Color(0xFFB9F27C).copy(alpha = a), 2.5f, Offset(x, y))
    }
}

private fun DrawScope.cosmos(t: Float) {
    for (i in 0 until 90) {
        val a = 0.2f + 0.8f * (0.5f + 0.5f * sin(t * TAU * (2 + i % 5) + i))
        drawCircle(Color.White.copy(alpha = a), 0.8f + rnd(i, 4) * 1.4f, Offset(size.width * rnd(i, 3), size.height * rnd(i, 5)))
    }
    val cycle = t * 3f
    val idx = floor(cycle).toInt()
    val f = cycle - idx
    if (f < 0.15f) {
        val k = f / 0.15f
        val sx = size.width * (0.3f + 0.6f * rnd(idx, 7)) - 160f * k
        val sy = size.height * 0.25f * rnd(idx, 8) + 90f * k
        drawLine(Color.White.copy(alpha = 1f - k), Offset(sx, sy), Offset(sx + 50f, sy - 28f), strokeWidth = 2.5f, cap = StrokeCap.Round)
    }
}

private fun DrawScope.sakura(t: Float) {
    for (i in 0 until 40) {
        val life = (t * (2 + (rnd(i, 1) * 3).toInt()) + rnd(i, 2)) % 1f
        val w = 7f + rnd(i, 4) * 7f
        drawOval(Color(0xFFFF9EC4).copy(alpha = 0.7f),
            Offset(size.width * rnd(i, 3) + sin(life * TAU * 2 + i) * 40f, size.height * (life * 1.1f - 0.05f)), Size(w, w * 0.6f))
    }
}

private fun DrawScope.matrix(t: Float) {
    val cols = 22
    val cw = size.width / cols
    for (c in 0 until cols) {
        val head = (t * (3 + (rnd(c, 1) * 4).toInt()) + rnd(c, 2)) % 1f
        for (k in 0 until 10) {
            val y = size.height * head - k * 22f
            if (y < 0f) continue
            drawRect(Color(0xFF00FF66).copy(alpha = (1f - k / 10f) * 0.55f), Offset(c * cw + cw * 0.35f, y), Size(cw * 0.3f, 14f))
        }
    }
}

private fun DrawScope.neon(t: Float) {
    val hz = size.height * 0.62f
    drawCircle(Brush.verticalGradient(listOf(Color(0xFFFFD166), Color(0xFFFF2BD6)), startY = hz - size.width * 0.28f, endY = hz),
        size.width * 0.22f, Offset(size.width / 2f, hz - size.width * 0.06f), alpha = 0.55f)
    drawRect(LineColors.Obsidian, Offset(0f, hz), Size(size.width, size.height - hz))
    for (k in 0 until 10) {
        val f = ((k + t * 10f) % 10f) / 10f
        val y = hz + (size.height - hz) * f * f
        drawLine(Color(0xFFFF2BD6).copy(alpha = 0.15f + 0.3f * f), Offset(0f, y), Offset(size.width, y), strokeWidth = 1.5f)
    }
    for (j in -8..8) {
        drawLine(Color(0xFF22D3EE).copy(alpha = 0.25f), Offset(size.width / 2f, hz),
            Offset(size.width / 2f + j * size.width * 0.18f, size.height), strokeWidth = 1.2f)
    }
}

private fun DrawScope.lava(t: Float) {
    drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color(0x66FF3B00)), startY = size.height * 0.55f, endY = size.height))
    for (i in 0 until 9) {
        val life = (t * (1 + i % 2) + rnd(i, 2)) % 1f
        val r = size.width * (0.12f + rnd(i, 4) * 0.12f)
        val c = Offset(size.width * rnd(i, 3) + sin(life * TAU + i) * 30f, size.height * (1.15f - life * 1.3f))
        drawCircle(Brush.radialGradient(listOf(Color(0xFFFF6A1F).copy(alpha = 0.35f), Color.Transparent), center = c, radius = r), r, c)
    }
}

private fun DrawScope.prism(t: Float) {
    val hues = listOf(Color(0xFFF472B6), Color(0xFF22D3EE), Color(0xFFA78BFA), Color(0xFF34D399), Color(0xFFFBBF24), Color(0xFF60A5FA))
    for (i in hues.indices) {
        val c = Offset(size.width * (0.5f + 0.38f * sin(t * TAU * (1 + i % 2) + i)),
            size.height * (0.5f + 0.35f * cos(t * TAU * (1 + (i + 1) % 2) + i * 2)))
        val r = size.width * 0.5f
        drawCircle(Brush.radialGradient(listOf(hues[i].copy(alpha = 0.22f), Color.Transparent), center = c, radius = r), r, c)
    }
    for (i in 0 until 30) {
        val a = 0.5f + 0.5f * sin(t * TAU * (2 + i % 4) + i)
        drawCircle(Color.White.copy(alpha = a * 0.6f), 1.5f, Offset(size.width * rnd(i, 3), size.height * rnd(i, 5)))
    }
}

private fun DrawScope.flowers(t: Float) {
    val cols = listOf(Color(0xFFFF8FB8), Color(0xFFFFD166), Color(0xFFC4B5FD), Color(0xFFFF9F68))
    for (i in 0 until 14) {
        val life = (t * (1 + i % 2) + rnd(i, 2)) % 1f
        val cx = size.width * rnd(i, 3) + sin(life * TAU + i) * 20f
        val cy = size.height * (1.1f - life * 1.2f)
        val r = 10f + rnd(i, 4) * 14f
        val a = sin(life * 3.1416f)
        rotate(life * 360f, Offset(cx, cy)) {
            for (k in 0 until 5) {
                val ang = k * TAU / 5f
                drawCircle(cols[i % 4].copy(alpha = 0.55f * a), r * 0.55f, Offset(cx + cos(ang) * r, cy + sin(ang) * r))
            }
        }
        drawCircle(Color(0xFFFFE08A).copy(alpha = 0.9f * a), r * 0.4f, Offset(cx, cy))
    }
}

private fun DrawScope.leaves(t: Float) {
    val cols = listOf(Color(0xFF4ADE80), Color(0xFF84CC16), Color(0xFFF59E0B), Color(0xFF22C55E))
    for (i in 0 until 26) {
        val life = (t * (2 + (rnd(i, 1) * 2).toInt()) + rnd(i, 2)) % 1f
        val x = size.width * rnd(i, 3) + sin(life * TAU * 2 + i) * 50f
        val y = size.height * (life * 1.1f - 0.05f)
        val w = 14f + rnd(i, 4) * 12f
        rotate(sin(life * TAU * 2 + i) * 50f, Offset(x, y)) {
            drawOval(cols[i % 4].copy(alpha = 0.6f), Offset(x - w / 2f, y - w / 4f), Size(w, w / 2f))
            drawLine(Color.Black.copy(alpha = 0.2f), Offset(x - w / 2f, y), Offset(x + w / 2f, y), 1f)
        }
    }
}

private fun DrawScope.droplets(t: Float) {
    val c = Color(0xFF7DD3FC)
    for (i in 0 until 14) {
        val life = (t * (3 + i % 3) + rnd(i, 2)) % 1f
        val x = size.width * rnd(i, 3)
        val gy = size.height * (0.35f + 0.6f * rnd(i, 4))
        if (life < 0.5f) {
            val y = gy * (life / 0.5f)
            drawLine(c.copy(alpha = 0.7f), Offset(x, y - 14f), Offset(x, y), strokeWidth = 3f, cap = StrokeCap.Round)
        } else {
            val f = (life - 0.5f) / 0.5f
            drawCircle(c.copy(alpha = (1f - f) * 0.6f), 6f + f * 44f, Offset(x, gy), style = Stroke(2f))
            drawCircle(c.copy(alpha = (1f - f) * 0.35f), 3f + f * 26f, Offset(x, gy), style = Stroke(1.5f))
        }
    }
}

private fun DrawScope.sky(t: Float) {
    drawRect(Brush.verticalGradient(listOf(Color(0xFF0B1D3A), Color(0xFF3B2A6B), Color(0x88FF9966)), startY = 0f, endY = size.height))
    val sun = Offset(size.width * 0.75f, size.height * 0.78f + sin(t * TAU) * 10f)
    drawCircle(Brush.radialGradient(listOf(Color(0xB3FFD27A), Color.Transparent), center = sun, radius = size.width * 0.35f), size.width * 0.35f, sun)
    for (i in 0 until 6) {
        val x = ((t * (1 + i % 2) + rnd(i, 3)) % 1f) * (size.width + 400f) - 200f
        val y = size.height * (0.1f + 0.5f * rnd(i, 4))
        val s = 30f + rnd(i, 5) * 30f
        for (k in 0 until 4) drawCircle(Color.White.copy(alpha = 0.12f), s * (0.7f + 0.15f * (k % 2)), Offset(x + k * s * 0.8f, y + (k % 2) * s * 0.15f))
    }
}

private fun DrawScope.moon(t: Float) {
    for (i in 0 until 70) {
        val a = 0.2f + 0.8f * (0.5f + 0.5f * sin(t * TAU * (2 + i % 5) + i))
        drawCircle(Color.White.copy(alpha = a), 0.8f + rnd(i, 4) * 1.3f, Offset(size.width * rnd(i, 3), size.height * rnd(i, 5)))
    }
    val r = size.width * 0.14f
    val c = Offset(size.width * 0.72f, size.height * 0.2f + sin(t * TAU) * 8f)
    drawCircle(Brush.radialGradient(listOf(Color(0x59FFF3C4), Color.Transparent), center = c, radius = r * 3f), r * 3f, c)
    drawCircle(Color(0xFFFFF3C4), r, c)
    drawCircle(Color(0x33B8A66A), r * 0.22f, Offset(c.x - r * 0.35f, c.y - r * 0.2f))
    drawCircle(Color(0x33B8A66A), r * 0.14f, Offset(c.x + r * 0.3f, c.y + r * 0.3f))
    drawCircle(Color(0x33B8A66A), r * 0.1f, Offset(c.x + r * 0.2f, c.y - r * 0.45f))
}

private fun DrawScope.cartoon(t: Float) {
    val fur = listOf(Color(0xFFFFB86B), Color(0xFFB8C0FF), Color(0xFFFF9EC4))
    val ink = Color(0xFF1A1405)
    for (i in 0 until 3) {
        val r = size.width * 0.09f
        val cx = size.width * (0.2f + 0.3f * i)
        val cy = size.height * (0.22f + 0.2f * (i % 2)) + sin(t * TAU * 2 + i * 2) * 14f
        val body = fur[i].copy(alpha = 0.55f)
        for (s in listOf(-1f, 1f)) {
            val p = Path()
            p.moveTo(cx + s * r * 0.95f, cy - r * 0.2f)
            p.lineTo(cx + s * r * 0.55f, cy - r * 1.35f)
            p.lineTo(cx + s * r * 0.1f, cy - r * 0.85f)
            p.close()
            drawPath(p, body)
        }
        drawCircle(body, r, Offset(cx, cy))
        val blink = if ((t * 6f + i) % 1f < 0.08f) 0.15f else 1f
        for (s in listOf(-1f, 1f)) {
            drawOval(ink.copy(alpha = 0.85f), Offset(cx + s * r * 0.38f - r * 0.1f, cy - r * 0.15f), Size(r * 0.2f, r * 0.28f * blink))
            drawCircle(Color(0xFFFF6B8A).copy(alpha = 0.5f), r * 0.13f, Offset(cx + s * r * 0.62f, cy + r * 0.25f))
        }
        drawArc(ink.copy(alpha = 0.8f), 20f, 140f, false, Offset(cx - r * 0.25f, cy + r * 0.05f), Size(r * 0.5f, r * 0.4f), style = Stroke(2.5f))
    }
    for (i in 0 until 20) {
        val a = 0.3f + 0.7f * (0.5f + 0.5f * sin(t * TAU * (2 + i % 4) + i))
        drawCircle(Color(0xFFFFE08A).copy(alpha = a * 0.7f), 2f, Offset(size.width * rnd(i, 3), size.height * rnd(i, 5)))
    }
}

private fun DrawScope.butterflies(t: Float) {
    val cols = listOf(Color(0xFFF472B6), Color(0xFF60A5FA), Color(0xFFFBBF24), Color(0xFFA78BFA))
    for (i in 0 until 8) {
        val cx = size.width * (0.5f + 0.4f * sin(t * TAU * (1 + i % 2) + i * 1.7f))
        val cy = size.height * (0.5f + 0.38f * cos(t * TAU * (1 + (i + 1) % 2) + i * 2.3f))
        val flap = 0.35f + 0.65f * abs(sin(t * TAU * 24f + i))
        val w = 16f + rnd(i, 4) * 10f
        val col = cols[i % 4].copy(alpha = 0.6f)
        drawOval(col, Offset(cx - w * flap, cy - w * 0.6f), Size(w * flap, w))
        drawOval(col, Offset(cx, cy - w * 0.6f), Size(w * flap, w))
        drawOval(col.copy(alpha = 0.4f), Offset(cx - w * flap * 0.7f, cy), Size(w * flap * 0.7f, w * 0.7f))
        drawOval(col.copy(alpha = 0.4f), Offset(cx, cy), Size(w * flap * 0.7f, w * 0.7f))
        drawLine(Color.White.copy(alpha = 0.5f), Offset(cx, cy - w * 0.5f), Offset(cx, cy + w * 0.5f), strokeWidth = 2f)
    }
}

private fun DrawScope.fireworks(t: Float) {
    val a = LineColors.Gold; val b = LineColors.Violet
    for (n in 0 until 4) {
        val idx = floor(t * 3f + n * 0.25f).toInt()
        val life = (t * 3f + n * 0.25f) % 1f
        val seed = n * 7 + idx % 3
        val cx = size.width * (0.15f + 0.7f * rnd(seed, 1))
        val cy = size.height * (0.15f + 0.4f * rnd(seed, 2))
        val r = life * size.width * 0.28f
        for (k in 0 until 24) {
            val ang = k * TAU / 24f
            drawCircle((if (k % 2 == 0) a else b).copy(alpha = (1f - life) * 0.9f), 3f * (1f - life * 0.5f),
                Offset(cx + cos(ang) * r, cy + sin(ang) * r + life * life * 60f))
        }
    }
}

private fun DrawScope.confetti(t: Float) {
    val cols = listOf(LineColors.Gold, LineColors.Violet, LineColors.Ivory)
    for (i in 0 until 50) {
        val life = (t * (2 + (rnd(i, 1) * 3).toInt()) + rnd(i, 2)) % 1f
        val c = Offset(size.width * rnd(i, 3) + sin(life * TAU * 2 + i) * 24f, size.height * (life * 1.1f - 0.05f))
        rotate(life * 720f + i * 30f, c) { drawRect(cols[i % 3].copy(alpha = 0.75f), Offset(c.x - 5f, c.y - 3f), Size(10f, 6f)) }
    }
}

private fun DrawScope.bubbles(t: Float) {
    for (i in 0 until 18) {
        val life = (t * (1 + i % 3) + rnd(i, 2)) % 1f
        val r = 14f + rnd(i, 4) * 36f
        val c = Offset(size.width * rnd(i, 3) + sin(life * TAU * 2 + i) * 16f, size.height * (1.1f - life * 1.25f))
        drawCircle((if (i % 2 == 0) LineColors.Gold else LineColors.Violet).copy(alpha = 0.55f * sin(life * 3.1416f)), r, c, style = Stroke(2f))
        drawCircle(Color.White.copy(alpha = 0.35f * sin(life * 3.1416f)), r * 0.15f, Offset(c.x - r * 0.4f, c.y - r * 0.4f))
    }
}

private fun DrawScope.hearts(t: Float) {
    for (i in 0 until 14) {
        val life = (t * (1 + i % 2) + rnd(i, 2)) % 1f
        val r = (10f + rnd(i, 4) * 16f) * (1f + 0.12f * sin(t * TAU * 6f + i))
        val x = size.width * rnd(i, 3) + sin(life * TAU * 2 + i) * 20f
        val y = size.height * (1.1f - life * 1.2f)
        val col = (if (i % 2 == 0) LineColors.Gold else LineColors.Violet).copy(alpha = 0.6f * sin(life * 3.1416f))
        drawCircle(col, r / 2f, Offset(x - r / 2f, y))
        drawCircle(col, r / 2f, Offset(x + r / 2f, y))
        val p = Path()
        p.moveTo(x - r * 0.98f, y + r * 0.12f); p.lineTo(x + r * 0.98f, y + r * 0.12f); p.lineTo(x, y + r * 1.15f); p.close()
        drawPath(p, col)
    }
}

private fun DrawScope.rings(t: Float) {
    val c = Offset(size.width * 0.5f, size.height * 0.45f)
    for (i in 0 until 6) {
        val life = (t * 3f + i / 6f) % 1f
        drawCircle((if (i % 2 == 0) LineColors.Gold else LineColors.Violet).copy(alpha = (1f - life) * 0.5f),
            life * size.width * 0.95f, c, style = Stroke(2.5f))
    }
}

private fun DrawScope.comet(t: Float) {
    for (i in 0 until 7) {
        val life = (t * (2 + i % 3) + rnd(i, 2)) % 1f
        val hx = (size.width + 200f) * life - 100f + rnd(i, 3) * 120f
        val hy = size.height * (0.1f + 0.8f * rnd(i, 4)) * 0.6f + life * size.height * 0.4f
        val col = if (i % 2 == 0) LineColors.Gold else LineColors.Violet
        for (k in 0 until 12) drawCircle(col.copy(alpha = (1f - k / 12f) * 0.6f), 4f - k * 0.25f, Offset(hx - k * 9f, hy - k * 5f))
    }
}

private fun DrawScope.spiral(t: Float) {
    val c = Offset(size.width * 0.5f, size.height * 0.4f)
    for (k in 0 until 120) {
        val ang = k * 0.35f + t * TAU
        val rad = k * size.width * 0.0045f
        drawCircle((if (k % 2 == 0) LineColors.Gold else LineColors.Violet).copy(alpha = 0.8f * (1f - k / 130f)), 1.5f + (120 - k) * 0.012f,
            Offset(c.x + cos(ang) * rad, c.y + sin(ang) * rad * 0.7f))
    }
}

private fun DrawScope.diamonds(t: Float) {
    for (i in 0 until 12) {
        val life = (t * (1 + i % 2) + rnd(i, 2)) % 1f
        val s = 10f + rnd(i, 4) * 22f
        val c = Offset(size.width * rnd(i, 3) + sin(life * TAU + i) * 20f, size.height * (1.1f - life * 1.2f))
        rotate(45f + life * 180f, c) {
            drawRect((if (i % 2 == 0) LineColors.Gold else LineColors.Violet).copy(alpha = 0.6f * sin(life * 3.1416f)),
                Offset(c.x - s, c.y - s), Size(2f * s, 2f * s), style = Stroke(2f))
        }
    }
}

private fun DrawScope.ribbons(t: Float) {
    for (w in 0 until 5) {
        val path = Path()
        val base = size.height * (0.25f + w * 0.14f)
        path.moveTo(0f, base)
        var x = 0f
        while (x <= size.width) {
            path.lineTo(x, base + 26f * sin(x / (110f + w * 25f) + t * TAU * (1 + w % 2) * (if (w % 2 == 0) 1 else -1)))
            x += 10f
        }
        drawPath(path, (if (w % 2 == 0) LineColors.Gold else LineColors.Violet).copy(alpha = 0.18f), style = Stroke(width = 14f, cap = StrokeCap.Round))
    }
}

private fun DrawScope.stardust(t: Float) {
    for (i in 0 until 80) {
        val life = (t * (1 + i % 3) + rnd(i, 2)) % 1f
        val a = 0.3f + 0.7f * (0.5f + 0.5f * sin(t * TAU * (2 + i % 4) + i))
        val col = when (i % 3) { 0 -> LineColors.Gold; 1 -> LineColors.Violet; else -> Color.White }
        drawCircle(col.copy(alpha = a * 0.7f), 1f + rnd(i, 4) * 2f, Offset(size.width * rnd(i, 3) + life * 90f - 45f, size.height * life))
    }
}

private fun DrawScope.starsBg(t: Float, n: Int, alphaMul: Float = 1f) {
    for (i in 0 until n) {
        val a = 0.2f + 0.8f * (0.5f + 0.5f * sin(t * TAU * (2 + i % 5) + i))
        drawCircle(Color.White.copy(alpha = a * alphaMul), 0.8f + rnd(i, 4) * 1.3f, Offset(size.width * rnd(i, 3), size.height * rnd(i, 5)))
    }
}

private fun DrawScope.cherry(t: Float) {
    val br = Path()
    br.moveTo(size.width * 1.05f, size.height * 0.05f)
    br.cubicTo(size.width * 0.8f, size.height * 0.1f, size.width * 0.6f, size.height * 0.08f, size.width * 0.35f, size.height * 0.2f)
    drawPath(br, Color(0xFF4A2A2A), style = Stroke(width = 9f, cap = StrokeCap.Round))
    for (i in 0 until 16) {
        val f = i / 15f
        val x = size.width * (1.0f - 0.65f * f)
        val y = size.height * (0.06f + 0.14f * f * f) + sin(t * TAU + i) * 3f
        val r = 7f + rnd(i, 1) * 6f
        for (k in 0 until 5) {
            val ang = k * TAU / 5f + sin(t * TAU) * 0.3f
            drawCircle(Color(0xFFFFC2DD).copy(alpha = 0.85f), r * 0.5f, Offset(x + cos(ang) * r * 0.6f, y + sin(ang) * r * 0.6f))
        }
        drawCircle(Color(0xFFFF7FAF), r * 0.25f, Offset(x, y))
    }
    sakura(t)
}

private fun DrawScope.solar(t: Float) {
    starsBg(t, 50)
    val c = Offset(size.width * 0.5f, size.height * 0.42f)
    val sunR = size.width * 0.07f
    drawCircle(Brush.radialGradient(listOf(Color(0xCCFFD166), Color.Transparent), center = c, radius = sunR * 4f), sunR * 4f, c)
    drawCircle(Color(0xFFFFC857), sunR, c)
    val cols = listOf(0xFFB1B1B1, 0xFFE8C07A, 0xFF4F9DFF, 0xFFE2674A, 0xFFD9A066, 0xFFE9D8A6, 0xFF7FDBDA, 0xFF3B6FD8)
    val cyc = listOf(24, 16, 12, 8, 5, 3, 2, 1)
    val rad = listOf(3f, 4f, 4.5f, 3.5f, 9f, 8f, 6f, 6f)
    for (i in 0 until 8) {
        val orbit = sunR * 1.8f + i * size.width * 0.052f
        drawOval(Color.White.copy(alpha = 0.10f), Offset(c.x - orbit, c.y - orbit * 0.55f), Size(orbit * 2f, orbit * 1.1f), style = Stroke(1f))
        val ang = t * TAU * cyc[i] + i * 0.9f
        drawCircle(Color(cols[i]), rad[i], Offset(c.x + cos(ang) * orbit, c.y + sin(ang) * orbit * 0.55f))
    }
}

private fun DrawScope.galaxy(t: Float) {
    starsBg(t, 50)
    val c = Offset(size.width * 0.5f, size.height * 0.4f)
    drawCircle(Brush.radialGradient(listOf(Color(0x66FFE9B0), Color.Transparent), center = c, radius = size.width * 0.3f), size.width * 0.3f, c)
    for (arm in 0 until 2) for (k in 0 until 110) {
        val ang = k * 0.12f + arm * 3.1416f + t * TAU
        val rad = k * size.width * 0.0045f
        val spread = rnd(k + arm * 200, 7) * 14f - 7f
        val col = if (k < 40) Color(0xFFFFE9B0) else Color(0xFF9DB7FF)
        drawCircle(col.copy(alpha = 0.75f * (1f - k / 130f)), 1.2f + rnd(k, 8) * 1.8f,
            Offset(c.x + cos(ang) * rad + spread, c.y + sin(ang) * rad * 0.6f + spread * 0.5f))
    }
}

private fun DrawScope.earthGlobe(t: Float) {
    starsBg(t, 40)
    val c = Offset(size.width * 0.5f, size.height * 0.38f)
    val r = size.width * 0.26f
    val lit = Offset(c.x - r * 0.3f, c.y - r * 0.3f)
    drawCircle(Brush.radialGradient(listOf(Color(0x664FA8FF), Color.Transparent), center = c, radius = r * 1.5f), r * 1.5f, c)
    drawCircle(Brush.radialGradient(listOf(Color(0xFF2F7CF0), Color(0xFF0B2A6B)), center = lit, radius = r * 1.4f), r, c)
    val disc = Path().apply { addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r)) }
    clipPath(disc) {
        for (i in 0 until 7) {
            val x = c.x - r + ((t + rnd(i, 1)) % 1f) * 2f * r
            val y = c.y + (rnd(i, 2) - 0.5f) * r * 1.3f
            drawOval(Color(0xFF3DBE6B).copy(alpha = 0.85f), Offset(x - r * 0.25f, y - r * 0.12f), Size(r * 0.5f * (0.6f + rnd(i, 3)), r * 0.25f))
        }
        drawCircle(Brush.radialGradient(listOf(Color.Transparent, Color(0x99000000)), center = lit, radius = r * 1.6f), r, c)
    }
    val a = t * TAU
    drawCircle(Color(0xFFDDDDDD), r * 0.1f, Offset(c.x + cos(a) * r * 1.6f, c.y + sin(a) * r * 0.5f))
}

private fun DrawScope.sunFlare(t: Float) {
    val c = Offset(size.width * 0.5f, size.height * 0.32f)
    val r = size.width * 0.17f
    drawCircle(Brush.radialGradient(listOf(Color(0x99FFB347), Color.Transparent), center = c, radius = r * 3f), r * 3f, c)
    for (i in 0 until 24) {
        val ang = i * TAU / 24f + t * TAU
        val len = r * (0.5f + 0.4f * sin(t * TAU * 6f + i * 2f))
        drawLine(Color(0xFFFFD27A).copy(alpha = 0.5f), Offset(c.x + cos(ang) * r * 1.05f, c.y + sin(ang) * r * 1.05f),
            Offset(c.x + cos(ang) * (r * 1.05f + len), c.y + sin(ang) * (r * 1.05f + len)), strokeWidth = 3f, cap = StrokeCap.Round)
    }
    drawCircle(Brush.radialGradient(listOf(Color(0xFFFFF3B0), Color(0xFFFF8A1F)), center = c, radius = r), r, c)
}

private fun DrawScope.eclipse(t: Float) {
    val c = Offset(size.width * 0.5f, size.height * 0.33f)
    val r = size.width * 0.16f
    val mx = c.x + sin(t * TAU) * r * 2.4f
    val cover = 1f - (abs(mx - c.x) / (r * 2f)).coerceIn(0f, 1f)
    starsBg(t, 50, 0.3f + 0.7f * cover)
    drawCircle(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.25f + 0.55f * cover), Color.Transparent), center = c, radius = r * (2.2f + cover)), r * (2.2f + cover), c)
    drawCircle(Color(0xFFFFF1C2), r, c)
    drawCircle(Color(0xFF05050A), r * 0.98f, Offset(mx, c.y))
}

private fun DrawScope.meteors(t: Float) {
    starsBg(t, 40)
    for (i in 0 until 14) {
        val life = (t * (6 + i % 4) + rnd(i, 2)) % 1f
        val hx = size.width * (0.2f + rnd(i, 3)) - life * size.width * 0.8f
        val hy = -40f + life * size.height * 1.1f
        for (k in 0 until 14) drawCircle(Color.White.copy(alpha = (1f - k / 14f) * 0.7f * sin(life * 3.1416f)), 2.6f - k * 0.14f, Offset(hx + k * 7f, hy - k * 11f))
    }
    drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color(0x442A5AA8)), startY = size.height * 0.75f, endY = size.height))
}

private fun DrawScope.saturn(t: Float) {
    starsBg(t, 50)
    val c = Offset(size.width * 0.5f, size.height * 0.38f)
    val r = size.width * 0.15f
    val ringTop = Offset(c.x - r * 2f, c.y - r * 0.45f)
    val ringSize = Size(r * 4f, r * 0.9f)
    val ring = Color(0xFFE9D8A6).copy(alpha = 0.6f)
    drawArc(ring, 180f, 180f, false, ringTop, ringSize, style = Stroke(10f))
    drawCircle(Brush.radialGradient(listOf(Color(0xFFF3D9A0), Color(0xFFB98A4A)), center = Offset(c.x - r * 0.3f, c.y - r * 0.3f), radius = r * 1.4f), r, c)
    drawArc(ring, 0f, 180f, false, ringTop, ringSize, style = Stroke(10f))
    for (i in 0 until 3) {
        val a = t * TAU * (i + 1) + i * 2f
        drawCircle(Color(0xFFDDDDDD), 4f + i, Offset(c.x + cos(a) * r * (2.4f + i * 0.4f), c.y + sin(a) * r * 0.9f))
    }
}

private fun DrawScope.nebula(t: Float) {
    val cols = listOf(Color(0xFFFF3D9A), Color(0xFF22D3EE), Color(0xFFFFA24A), Color(0xFF8B5CF6))
    for (i in 0 until 6) {
        val c = Offset(size.width * (0.5f + 0.35f * sin(t * TAU + i * 1.3f)), size.height * (0.45f + 0.3f * cos(t * TAU + i * 2.1f)))
        val r = size.width * 0.55f
        drawCircle(Brush.radialGradient(listOf(cols[i % 4].copy(alpha = 0.16f), Color.Transparent), center = c, radius = r), r, c)
    }
    starsBg(t, 70)
}

private fun DrawScope.warp(t: Float) {
    val c = Offset(size.width * 0.5f, size.height * 0.45f)
    for (i in 0 until 90) {
        val life = (t * (3 + i % 4) + rnd(i, 2)) % 1f
        val ang = rnd(i, 3) * TAU
        val d0 = life * life * size.width * 0.9f
        val d1 = d0 + life * 40f + 4f
        drawLine(Color.White.copy(alpha = life * 0.8f), Offset(c.x + cos(ang) * d0, c.y + sin(ang) * d0),
            Offset(c.x + cos(ang) * d1, c.y + sin(ang) * d1), strokeWidth = 1f + life * 2f, cap = StrokeCap.Round)
    }
}
