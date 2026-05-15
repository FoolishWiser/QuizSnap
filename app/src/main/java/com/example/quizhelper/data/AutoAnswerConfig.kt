package com.example.quizhelper.data

import android.content.Context
import android.content.SharedPreferences
import java.util.Random

/**
 * 自动答题配置
 */
data class AutoAnswerConfig(
    val totalQuestions: Int = 10,
    val minTotalTimeSec: Int = 30
) {
    /**
     * 生成每题的随机点击时间（毫秒），保证总时间 ≥ minTotalTimeSec + 10 秒
     */
    fun generateClickTimes(): List<Long> {
        val random = Random()
        val minMs = 600L   // 每题最少 0.6 秒
        val maxMs = 3500L  // 每题最多 3.5 秒
        val minTotalMs = (minTotalTimeSec + 10) * 1000L

        // 生成初始随机值
        val times = MutableList(totalQuestions) {
            minMs + (random.nextLong() and Long.MAX_VALUE) % (maxMs - minMs + 1)
        }

        // 如果总时间不足，按比例放大
        val currentTotal = times.sum()
        if (currentTotal < minTotalMs && currentTotal > 0) {
            val scale = minTotalMs.toDouble() / currentTotal
            return times.map { (it * scale).toLong().coerceAtLeast(minMs) }
        }
        return times
    }

    companion object {
        private const val PREFS_NAME = "auto_answer_config"
        private const val KEY_TOTAL_QUESTIONS = "total_questions"
        private const val KEY_MIN_TIME = "min_total_time"
        private const val KEY_SHIZUKU_ENABLED = "shizuku_enabled"

        fun load(context: Context): AutoAnswerConfig {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return AutoAnswerConfig(
                totalQuestions = prefs.getInt(KEY_TOTAL_QUESTIONS, 10),
                minTotalTimeSec = prefs.getInt(KEY_MIN_TIME, 30)
            )
        }

        fun save(context: Context, config: AutoAnswerConfig) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_TOTAL_QUESTIONS, config.totalQuestions)
                .putInt(KEY_MIN_TIME, config.minTotalTimeSec)
                .apply()
        }

        fun isShizukuEnabled(context: Context): Boolean {
            return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_SHIZUKU_ENABLED, false)
        }

        fun setShizukuEnabled(context: Context, enabled: Boolean) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_SHIZUKU_ENABLED, enabled)
                .apply()
        }
    }
}
