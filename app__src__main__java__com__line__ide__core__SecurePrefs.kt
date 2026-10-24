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
    var theme: Int
        get() = prefs.getInt("theme", 0)
        set(v) = prefs.edit().putInt("theme", v).apply()
    var timeout: Int
        get() = prefs.getInt("timeout", 30)
        set(v) = prefs.edit().putInt("timeout", v.coerceIn(1, 300)).apply()
}
