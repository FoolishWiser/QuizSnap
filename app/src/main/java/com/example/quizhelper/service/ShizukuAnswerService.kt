package com.example.quizhelper.service

import android.content.Context
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.example.quizhelper.data.AutoAnswerConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.DataOutputStream

/**
 * 基于 Shizuku 的自动答题服务。
 * 使用 Shell 权限模拟触摸点击，适用于不支持无障碍服务的场景。
 */
object ShizukuAnswerService {

    private const val TAG = "ShizukuAnswer"

    /** 是否已初始化（Shizuku 连接就绪） */
    @Volatile
    var isInitialized = false
        private set

    /** Shizuku 进程输出流 */
    private var shellOutput: DataOutputStream? = null

    /** Shizuku 进程 */
    private var shizukuProcess: Process? = null

    /**
     * 初始化 Shizuku 连接
     */
    suspend fun init(context: Context): Boolean {
        if (isInitialized) return true
        return withContext(Dispatchers.IO) {
            try {
                // 获取 Shizuku 服务 Binder
                val binder = shizukuBinder
                if (binder == null) {
                    Log.w(TAG, "Shizuku not available")
                    return@withContext false
                }

                // 通过 Shizuku 创建子进程执行 shell 命令
                val process = ProcessBuilder("sh")
                    .redirectErrorStream(true)
                    .start()

                val output = DataOutputStream(process.outputStream)
                shellOutput = output
                shizukuProcess = process

                // 验证可用性
                output.writeBytes("echo SHIZUKU_OK\n")
                output.flush()

                isInitialized = true
                Log.d(TAG, "Shizuku initialized successfully")
                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize Shizuku: ${e.message}")
                cleanup()
                false
            }
        }
    }

    /**
     * 执行一次自动答题流程
     * @param clickTimes 每题点击时间间隔列表（毫秒）
     * @param onProgress 进度回调（当前题目索引）
     * @param onComplete 完成回调
     */
    suspend fun executeAutoAnswer(
        clickTimes: List<Long>,
        onProgress: (Int) -> Unit = {},
        onComplete: () -> Unit = {}
    ) {
        if (!isInitialized) {
            Log.w(TAG, "Shizuku not initialized")
            return
        }

        withContext(Dispatchers.IO) {
            try {
                for (i in clickTimes.indices) {
                    if (!isInitialized) break

                    onProgress(i)

                    // 随机延迟后执行点击
                    delay(clickTimes[i])

                    // 在屏幕中心点击（答题 app 的选项通常在中间区域）
                    // 由于 Shizuku 有 shell 权限，可以用 input tap 模拟点击
                    executeShellCommand("input tap 540 1200")

                    // 等待点击完成
                    delay(500)

                    // 点击下一题按钮（通常在屏幕底部）
                    executeShellCommand("input tap 540 1800")

                    // 等待页面切换
                    delay(800)
                }

                onComplete()
            } catch (e: Exception) {
                Log.e(TAG, "Auto answer error: ${e.message}")
            }
        }
    }

    /** 执行精简版流程，使用配置随机时间 */

    /**
     * 执行一遍答题流程
     * @param config 答题配置
     * @param coordinates 可选的自定义点击坐标对列表 (x, y)，为 null 则使用默认位置
     * @param onProgress 进度回调
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
            540 to 1200,  // 选项区域（中心偏下）
            540 to 1800   // 下一题按钮
        )

        withContext(Dispatchers.IO) {
            try {
                for (i in 0 until config.totalQuestions) {
                    if (!isInitialized) break
                    onProgress(i)

                    delay(clickTimes[i])

                    // 点击选项
                    executeShellCommand("input tap ${defaultCoords[0].first} ${defaultCoords[0].second}")
                    delay(600)

                    // 点击下一题
                    executeShellCommand("input tap ${defaultCoords[1].first} ${defaultCoords[1].second}")
                    delay(800)
                }
                onComplete()
            } catch (e: Exception) {
                Log.e(TAG, "Execute error: ${e.message}")
            }
        }
    }

    /** 通过 Shell 执行命令 */
    private fun executeShellCommand(cmd: String) {
        try {
            shellOutput?.writeBytes("$cmd\n")
            shellOutput?.flush()
        } catch (e: Exception) {
            Log.e(TAG, "Shell exec error: $cmd - ${e.message}")
        }
    }

    /** 清理资源 */
    fun cleanup() {
        isInitialized = false
        try {
            shellOutput?.close()
            shizukuProcess?.destroy()
        } catch (_: Exception) {}
        shellOutput = null
        shizukuProcess = null
    }

    /** Shizuku Binder 实例（由框架注入） */
    var shizukuBinder: IBinder? = null

    /** 检查 Shizuku 是否已授权 */
    fun isPermissionGranted(): Boolean = isInitialized
}
