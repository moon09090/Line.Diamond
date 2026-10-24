package com.line.ide.core

import java.util.concurrent.LinkedBlockingQueue

/** Lets Python's input() pause and ask the user through a dialog in the app. */
object InputBridge {
    @Volatile var onPrompt: ((String) -> Unit)? = null
    private val answers = LinkedBlockingQueue<String>()

    @JvmStatic
    fun request(prompt: String): String {
        val cb = onPrompt ?: return ""
        cb(prompt)
        return try { answers.take() } catch (e: InterruptedException) { "" }
    }

    fun answer(text: String) { answers.offer(text) }
    fun reset() { answers.clear() }
}
