package com.line.ide.core

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore

/** Renders code as a dark, gold-titled PNG saved to Pictures/Line (Android 10+). */
object CodeImage {
    fun save(ctx: Context, title: String, code: String): Uri? {
        if (Build.VERSION.SDK_INT < 29) return null
        val lines = code.lines().take(60)
        val pad = 40f
        val lh = 38f
        val h = (pad * 2 + 70f + lines.size * lh).toInt()
        val bmp = Bitmap.createBitmap(1200, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        c.drawColor(Color.parseColor("#0E0E12"))
        val gold = Paint().apply { color = Color.parseColor("#D4AF37"); textSize = 34f; typeface = Typeface.MONOSPACE; isAntiAlias = true }
        val txt = Paint(gold).apply { color = Color.parseColor("#E8E6F0"); textSize = 30f }
        c.drawText("Line  -  $title", pad, pad + 34f, gold)
        lines.forEachIndexed { i, l -> c.drawText(l.take(60), pad, pad + 90f + i * lh, txt) }
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "line_${System.currentTimeMillis()}.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Line")
        }
        val uri = ctx.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return null
        ctx.contentResolver.openOutputStream(uri)?.use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
        return uri
    }
}
