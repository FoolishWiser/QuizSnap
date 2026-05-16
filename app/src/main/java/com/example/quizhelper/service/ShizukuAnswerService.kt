package com.example.quizhelper.service

import android.content.Context
import android.util.Log
import com.example.quizhelper.data.AutoAnswerConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader

/**
 * 基于 Shizuku 提权 Shell 的自动答题服务。
 * 使用 `su` 命令获取 Shell 权限模拟点击，适用于已安装 Shizuku/Root 的设备。
 */
object ShizukuAnswerService {

    private const val TAG = "ShizukuAnswer"

    @Volatile
    var isInitialized = false
        private set

    private var shellOutput: DataOutputStream? = null
    private var shellInput: BufferedReader? = null
    private var shizukuProcess: Process? = null

    /**
     * 初始化提权 Shell
     */
    suspend fun init(context: Context): Boolean {
        if (isInitialized) return true
        return withContext(Dispatchers.IO) {
            try {
                // 先尝试通过 Shizuku 的 su 创建进程
                val process = tryShizukuProcess() ?: return@withContext false

                shizukuProcess = process
                shellOutput = DataOutputStream(process.outputStream)
                shellInput = BufferedReader(InputStreamReader(process.inputStream))

                // 验证 Shell 可用
                shellOutput?.writeBytes("echo SHIZUKU_OK\n")
                shellOutput?.flush()

                val response = shellInput?.readLine()
                if (response == "SHIZUKU_OK") {
                    isInitialized = true
                    Log.d(TAG, "Shell initialized successfully")
                    true
                } else {
                    Log.w(TAG, "Shell verification failed: $response")
                    cleanup()
                    false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to init: ${e.message}")
                cleanup()
                false
            }
        }
    }

    /** 尝试通过 Shizuku/ADB su 创建提权进程 */
    private fun tryShizukuProcess(): Process? {
        val commands = listOf("su", "sh")
        for (cmd in commands) {
            try {
                val p = Runtime.getRuntime().exec(arrayOf(cmd))
                // 测试是否可用
                val out = DataOutputStream(p.outputStream)
                val reader = BufferedReader(InputStreamReader(p.inputStream))
                out.writeBytes("id\n")
                out.flush()
                val line = reader.readLine()
                if (line != null && line.contains("uid=0")) {
                    // 激活 Shell
                    out.writeBytes("sh\n")
                    out.flush()
                    // 丢弃之前的输出
                    Thread.sleep(100)
                    while (reader.ready()) reader.readLine()
                    return p
                }
                p.destroy()
            } catch (_: Exception) {}
        }
        // 兜底：如果 su 不可用，试普通 sh（权限较低但能工作）
        return try {
            Runtime.getRuntime().exec(arrayOf("sh"))
        } catch (_: Exception) {
            null
        }
    }

    /**
     * 执行答题流程
     */
    suspend fun executeWithConfig(
        config: AutoAnswerConfig,
        coordinates: List<Pair<Int, Int>>? = null,
        onProgress: (Int) -> Unit = {},
        onComplete: () -> Unit = {}
    ) {
        val clickTimes = config.generateClickTimes()
        Log.d(TAG, "Click times: ${clickTimes.joinToString(", ")} (total: ${clickTimes.sum()}ms)")

        val defaultCoords = coordinates ?: listOf(
            540 to 1200,
            540 to 1800
        )

        withContext(Dispatchers.IO) {
            try {
                for (i in 0 until config.totalQuestions) {
                    if (!isInitialized) break
                    onProgress(i)
                    delay(clickTimes[i])
                    executeCmd("input tap ${defaultCoords[0].first} ${defaultCoords[0].second}")
                    delay(600)
                    executeCmd("input tap ${defaultCoords[1].first} ${defaultCoords[1].second}")
                    delay(800)
                }
                onComplete()
            } catch (e: Exception) {
                Log.e(TAG, "Execute error: ${e.message}")
            }
        }
    }

    private fun executeCmd(cmd: String) {
        try {
            shellOutput?.writeBytes("$cmd\n")
            shellOutput?.flush()
        } catch (e: Exception) {
            Log.e(TAG, "Exec error: $cmd - ${e.message}")
        }
    }

    fun cleanup() {
        isInitialized = false
        try { shellOutput?.close() } catch (_: Exception) {}
        try { shellInput?.close() } catch (_: Exception) {}
        try { shizukuProcess?.destroy() } catch (_: Exception) {}
        shellOutput = null
        shellInput = null
        shizukuProcess = null
    }
}

