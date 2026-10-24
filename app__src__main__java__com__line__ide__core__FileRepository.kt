package com.line.ide.core

import android.content.Context
import java.io.File

class FileRepository(context: Context) {
    private val root = File(context.filesDir, "workspace").apply { mkdirs() }

    private fun safe(name: String) = name.replace(Regex("[^A-Za-z0-9_.-]"), "_").let {
        if (it.endsWith(".py")) it else "$it.py"
    }

    fun list(): List<String> =
        root.listFiles { f -> f.extension == "py" }?.sortedByDescending { it.lastModified() }?.map { it.name } ?: emptyList()

    fun read(name: String): String = File(root, safe(name)).takeIf { it.exists() }?.readText() ?: ""
    fun save(name: String, content: String) = File(root, safe(name)).writeText(content)
    fun delete(name: String) = File(root, safe(name)).delete()
    fun rename(old: String, new: String): Boolean = File(root, safe(old)).renameTo(File(root, safe(new)))
    fun exists(name: String) = File(root, safe(name)).exists()
    fun normalize(name: String) = safe(name)
}
