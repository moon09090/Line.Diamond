package com.line.ide.core

import com.chaquo.python.Python
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

data class RunResult(val stdout: String, val stderr: String, val elapsedMs: Int, val ok: Boolean,
                     val timedOut: Boolean, val plots: List<String>)
data class SyntaxIssue(val line: Int, val msg: String)

object PythonEngine {
    private val module by lazy { Python.getInstance().getModule("line_runner") }
    val version: String by lazy { module.callAttr("python_version").toString() }

    suspend fun run(code: String, stdin: String, timeoutSec: Double, plotDir: String): RunResult =
        withContext(Dispatchers.IO) {
            InputBridge.reset()
            val j = JSONObject(module.callAttr("run", code, stdin, timeoutSec, plotDir).toString())
            val p = j.getJSONArray("plots")
            RunResult(j.getString("stdout"), j.getString("stderr"), j.getInt("elapsed_ms"),
                j.getBoolean("ok"), j.getBoolean("timed_out"), List(p.length()) { p.getString(it) })
        }

    suspend fun tidy(code: String): String = withContext(Dispatchers.IO) { module.callAttr("tidy", code).toString() }

    suspend fun check(code: String): SyntaxIssue? = withContext(Dispatchers.IO) {
        val j = JSONObject(module.callAttr("check_syntax", code).toString())
        if (j.getBoolean("ok")) null else SyntaxIssue(j.getInt("line"), j.getString("msg"))
    }
}
