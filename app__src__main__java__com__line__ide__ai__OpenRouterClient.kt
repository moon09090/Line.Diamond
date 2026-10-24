package com.line.ide.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

enum class AiAction(val label: String, val prompt: String) {
    EXPLAIN("Explain", "Explain this Python code clearly and concisely."),
    FIX("Fix", "Find and fix the bug(s). Show the corrected full code and a one-line reason."),
    IMPROVE("Improve", "Refactor for readability, performance and PEP 8. Show the full improved code."),
    ASK("Ask", "Answer the user's question about this code.")
}

object OpenRouterClient {
    private val http = OkHttpClient.Builder().callTimeout(60, TimeUnit.SECONDS).build()

    suspend fun ask(apiKey: String, model: String, action: AiAction, code: String, extra: String): String =
        withContext(Dispatchers.IO) {
            if (apiKey.isBlank()) return@withContext "Add your OpenRouter API key in Settings first."
            val user = "${action.prompt}\n\n```python\n$code\n```\n\n$extra"
            val body = JSONObject().put("model", model).put("messages", JSONArray()
                .put(JSONObject().put("role", "system").put("content",
                    "You are Line, an expert Python mentor inside a mobile IDE. Be concise."))
                .put(JSONObject().put("role", "user").put("content", user)))
                .toString().toRequestBody("application/json".toMediaType())
            val req = Request.Builder().url("https://openrouter.ai/api/v1/chat/completions")
                .header("Authorization", "Bearer $apiKey").post(body).build()
            try {
                http.newCall(req).execute().use { r ->
                    val txt = r.body?.string().orEmpty()
                    if (!r.isSuccessful) "AI error ${r.code}: ${txt.take(200)}"
                    else JSONObject(txt).getJSONArray("choices").getJSONObject(0)
                        .getJSONObject("message").getString("content")
                }
            } catch (e: Exception) { "Network error: ${e.message}" }
        }
}
