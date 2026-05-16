package com.example.quizhelper.utils

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * GitHub 更新检测工具
 */
object UpdateChecker {

    private const val TAG = "UpdateChecker"
    private const val GITHUB_API = "https://api.github.com/repos/FoolishWiser/QuizSnap/releases/latest"

    /** GitHub 镜像加速列表 */
    val mirrors = listOf(
        Mirror("直连 (GitHub)", ""),
        Mirror("ghproxy.com", "https://ghproxy.com/"),
        Mirror("gh.api.99988866.xyz", "https://gh.api.99988866.xyz/"),
        Mirror("gh.ddlc.top", "https://gh.ddlc.top/"),
        Mirror("github.moeyy.xyz", "https://github.moeyy.xyz/")
    )

    data class Mirror(val name: String, val prefix: String)
    data class UpdateInfo(
        val latestVersion: String,
        val downloadUrl: String,
        val releaseNotes: String,
        val publishedAt: String
    )

    /**
     * 检测是否有新版本
     * @param currentVersion 当前版本号（如 "1.1.0"）
     * @param mirrorIndex 镜像索引（0=直连）
     * @return UpdateInfo 或 null（出错/无新版本时返回 null）
     */
    suspend fun checkUpdate(currentVersion: String, mirrorIndex: Int = 0): UpdateInfo? {
        return withContext(Dispatchers.IO) {
            try {
                val mirror = if (mirrorIndex in mirrors.indices) mirrors[mirrorIndex] else mirrors[0]
                val apiUrl = if (mirrorIndex == 0) GITHUB_API else "${mirror.prefix}$GITHUB_API"

                Log.d(TAG, "Checking update via: ${mirror.name} -> $apiUrl")

                val json = fetchJson(apiUrl) ?: return@withContext null
                val tagName = json.optString("tag_name", "") ?: ""
                val latestVer = tagName.removePrefix("v").removePrefix("V")

                // 比较版本号
                if (compareVersions(latestVer, currentVersion) <= 0) {
                    Log.d(TAG, "No update: current=$currentVersion, latest=$latestVer")
                    return@withContext null
                }

                // 获取 APK 下载地址
                val assets = json.optJSONArray("assets")
                var downloadUrl = ""
                if (assets != null && assets.length() > 0) {
                    for (i in 0 until assets.length()) {
                        val asset = assets.getJSONObject(i)
                        val name = asset.optString("name", "")
                        if (name.endsWith(".apk")) {
                            val rawUrl = asset.optString("browser_download_url", "")
                            downloadUrl = if (mirrorIndex == 0) rawUrl else "${mirror.prefix}$rawUrl"
                            break
                        }
                    }
                }

                UpdateInfo(
                    latestVersion = latestVer,
                    downloadUrl = downloadUrl,
                    releaseNotes = json.optString("body", ""),
                    publishedAt = json.optString("published_at", "")
                )
            } catch (e: Exception) {
                Log.e(TAG, "Check update failed: ${e.message}")
                null
            }
        }
    }

    private fun fetchJson(urlStr: String): JSONObject? {
        var conn: HttpURLConnection? = null
        try {
            val url = URL(urlStr)
            conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 10000
            conn.readTimeout = 10000
            conn.setRequestProperty("Accept", "application/vnd.github+json")
            conn.setRequestProperty("User-Agent", "QuizSnap-Android")

            val code = conn.responseCode
            if (code != 200) {
                Log.w(TAG, "HTTP $code for $urlStr")
                return null
            }

            val reader = BufferedReader(InputStreamReader(conn.inputStream))
            val sb = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                sb.append(line)
            }
            return JSONObject(sb.toString())
        } catch (e: Exception) {
            Log.e(TAG, "Fetch JSON error: ${e.message}")
            return null
        } finally {
            conn?.disconnect()
        }
    }

    /** 比较语义化版本号，返回 -1/0/1 */
    private fun compareVersions(v1: String, v2: String): Int {
        val parts1 = v1.split(".").map { it.toIntOrNull() ?: 0 }
        val parts2 = v2.split(".").map { it.toIntOrNull() ?: 0 }
        for (i in 0 until maxOf(parts1.size, parts2.size)) {
            val a = parts1.getOrElse(i) { 0 }
            val b = parts2.getOrElse(i) { 0 }
            if (a != b) return a - b
        }
        return 0
    }
}
