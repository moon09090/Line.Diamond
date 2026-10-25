package com.line.ide.core

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.PI
import kotlin.math.sin

/** Short synthesised sound effects for themes. Switchable on/off from the menu or Settings. */
object ThemeSound {
    @Volatile var enabled = false
    private const val RATE = 22050

    private fun render(ms: Int, noise: Float, freq: (Double) -> Double): ShortArray {
        val n = RATE * ms / 1000
        var phase = 0.0
        var lp = 0.0
        val rnd = java.util.Random(7)
        return ShortArray(n) { i ->
            val t = i.toDouble() / RATE
            val f = freq(t)
            val env = sin(PI * i / n)
            phase += 2 * PI * f / RATE
            lp += (rnd.nextDouble() * 2 - 1 - lp) * 0.2
            val tone = if (f > 0) sin(phase) * (1 - noise) else 0.0
            ((tone + lp * noise * 2) * env * 9000).toInt().coerceIn(-32000, 32000).toShort()
        }
    }

    private fun play(pcm: ShortArray) {
        if (!enabled) return
        try {
            val track = AudioTrack.Builder()
                .setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build())
                .setAudioFormat(AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(RATE).setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build())
                .setBufferSizeInBytes(pcm.size * 2).setTransferMode(AudioTrack.MODE_STATIC).build()
            track.write(pcm, 0, pcm.size)
            track.play()
            Thread { try { Thread.sleep(pcm.size * 1000L / RATE + 400); track.release() } catch (e: Exception) { } }.start()
        } catch (e: Exception) { }
    }

    fun theme(category: String) {
        val pcm = when (category) {
            "Space" -> render(800, 0f) { t -> 180.0 + 500.0 * t }
            "Nature" -> render(600, 0.85f) { _ -> 0.0 }
            "Weather" -> render(700, 0.95f) { _ -> 0.0 }
            else -> render(450, 0f) { t -> if (t < 0.15) 523.0 else if (t < 0.3) 659.0 else 784.0 }
        }
        play(pcm)
    }

    fun chime() = play(render(350, 0f) { t -> if (t < 0.15) 880.0 else 1320.0 })
}
