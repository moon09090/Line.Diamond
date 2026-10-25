package com.line.ide.core

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/** Settings and API key are stored encrypted (AES-256, Keystore-backed). */
class SecurePrefs(context: Context) {
    private val prefs = EncryptedSharedPreferences.create(
        context, "line_secure",
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    var apiKey: String
        get() = prefs.getString("api_key", "") ?: ""
        set(v) = prefs.edit().putString("api_key", v.trim()).apply()
    var model: String
        get() = prefs.getString("model", "openai/gpt-4o-mini") ?: "openai/gpt-4o-mini"
        set(v) = prefs.edit().putString("model", v.trim()).apply()
    var fontSize: Int
        get() = prefs.getInt("font", 14)
        set(v) = prefs.edit().putInt("font", v.coerceIn(10, 28)).apply()
    var animate: Boolean
        get() = prefs.getBoolean("anim", true)
        set(v) = prefs.edit().putBoolean("anim", v).apply()
    var runs: Int
        get() = prefs.getInt("runs", 0)
        set(v) = prefs.edit().putInt("runs", v).apply()
    var lastDay: Int
        get() = prefs.getInt("lastDay", 0)
        set(v) = prefs.edit().putInt("lastDay", v).apply()
    var streak: Int
        get() = prefs.getInt("streak", 0)
        set(v) = prefs.edit().putInt("streak", v).apply()
    var snips: String
        get() = prefs.getString("snips", "") ?: ""
        set(v) = prefs.edit().putString("snips", v).apply()
    var sound: Boolean
        get() = prefs.getBoolean("sound", false)
        set(v) = prefs.edit().putBoolean("sound", v).apply()
    var onboarded: Boolean
        get() = prefs.getBoolean("onboarded", false)
        set(v) = prefs.edit().putBoolean("onboarded", v).apply()
    var favs: String
        get() = prefs.getString("favs", "") ?: ""
        set(v) = prefs.edit().putString("favs", v).apply()
    var theme: Int
        get() = prefs.getInt("theme", 0)
        set(v) = prefs.edit().putInt("theme", v).apply()
    var timeout: Int
        get() = prefs.getInt("timeout", 30)
        set(v) = prefs.edit().putInt("timeout", v.coerceIn(1, 300)).apply()
}
