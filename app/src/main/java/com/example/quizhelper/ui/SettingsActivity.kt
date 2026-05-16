package com.example.quizhelper.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.quizhelper.R
import com.example.quizhelper.data.AutoAnswerConfig
import com.example.quizhelper.databinding.ActivitySettingsBinding
import com.example.quizhelper.service.AutoAnswerService
import com.example.quizhelper.service.ShizukuAnswerService
import com.example.quizhelper.utils.UpdateChecker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    companion object {
        const val PREFS_NAME = "quiz_helper_prefs"
        const val KEY_AUTO_ANSWER = "auto_answer_enabled"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener { finish() }

        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        // ---------- 无障碍自动答题 ----------
        binding.switchAutoAnswer.isChecked = prefs.getBoolean(KEY_AUTO_ANSWER, false)
        binding.switchAutoAnswer.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!isAccessibilityServiceEnabled()) {
                    showAccessibilityDialog()
                } else {
                    savePreference(true)
                    AutoAnswerService.isEnabled = true
                    Toast.makeText(this, "自动答题已开启", Toast.LENGTH_SHORT).show()
                }
            } else {
                savePreference(false)
                AutoAnswerService.isEnabled = false
                Toast.makeText(this, "自动答题已关闭", Toast.LENGTH_SHORT).show()
            }
        }

        // ---------- Shizuku 模式 ----------
        val shizukuEnabled = AutoAnswerConfig.isShizukuEnabled(this)
        binding.switchShizuku.isChecked = shizukuEnabled
        binding.switchShizuku.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                lifecycleScope.launch {
                    val ok = ShizukuAnswerService.init(this@SettingsActivity)
                    if (ok) {
                        AutoAnswerConfig.setShizukuEnabled(this@SettingsActivity, true)
                        Toast.makeText(this@SettingsActivity, "Shizuku 模式已开启", Toast.LENGTH_SHORT).show()
                        updateStatusText()
                        showShizukuConfigDialog()
                    } else {
                        binding.switchShizuku.isChecked = false
                        showShizukuGuideDialog()
                    }
                }
            } else {
                AutoAnswerConfig.setShizukuEnabled(this, false)
                ShizukuAnswerService.cleanup()
                Toast.makeText(this, "Shizuku 模式已关闭", Toast.LENGTH_SHORT).show()
                updateStatusText()
            }
        }

        binding.btnShizukuConfig.setOnClickListener { showShizukuConfigDialog() }
        updateShizukuConfigButton()

        // ---------- 检测更新 ----------
        binding.btnCheckUpdate.setOnClickListener { checkForUpdate() }

        updateStatusText()
    }

    // ==================== 无障碍 ====================

    private fun savePreference(enabled: Boolean) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .edit().putBoolean(KEY_AUTO_ANSWER, enabled).apply()
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val serviceName = "${packageName}/.service.AutoAnswerService"
        val enabledServices = Settings.Secure.getString(
            contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        return enabledServices?.contains(serviceName) == true
    }

    private fun showAccessibilityDialog() {
        AlertDialog.Builder(this)
            .setTitle("需要无障碍权限")
            .setMessage("自动答题功能需要开启无障碍权限。开启后，应用将能够：\n\n1. 识别屏幕上的题目内容\n2. 自动点击匹配的答案选项\n3. 自动进入下一题\n\n请放心，应用不会收集或上传您的任何个人信息。")
            .setPositiveButton("去设置") { _: android.content.DialogInterface, _: Int ->
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                Toast.makeText(this, "请找到「自动答题服务」并开启", Toast.LENGTH_LONG).show()
            }
            .setNegativeButton("取消") { _: android.content.DialogInterface, _: Int ->
                binding.switchAutoAnswer.isChecked = false
            }
            .setOnCancelListener {
                binding.switchAutoAnswer.isChecked = false
            }
            .show()
    }

    // ==================== Shizuku ====================

    private fun showShizukuGuideDialog() {
        AlertDialog.Builder(this)
            .setTitle("需要 Shizuku")
            .setMessage("Shizuku 模式需要安装 Shizuku 应用并授予权限。\n\n" +
                    "请先安装 Shizuku（酷安/官网下载），然后在 Shizuku 应用中启动服务后返回。")
            .setPositiveButton("了解", null)
            .show()
    }

    private fun showShizukuConfigDialog() {
        val config = AutoAnswerConfig.load(this)
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(32, 24, 32, 24)
        }

        val countInput = android.widget.EditText(this).apply {
            setText(config.totalQuestions.toString())
            hint = "答题总数量"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 16) }
        }
        val timeInput = android.widget.EditText(this).apply {
            setText(config.minTotalTimeSec.toString())
            hint = "最低总时间（秒）"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }

        layout.addView(android.widget.TextView(this).apply { text = "答题数量"; textSize = 14f })
        layout.addView(countInput)
        layout.addView(android.widget.TextView(this).apply {
            text = "最低总时间（秒）"; textSize = 14f; setPadding(0, 8, 0, 0)
        })
        layout.addView(timeInput)
        layout.addView(android.widget.TextView(this).apply {
            text = "\n系统将自动为每题生成随机点击时间，保证总时间 ≥ 设定值 + 10 秒"
            textSize = 12f; setTextColor(0xFF79747E.toInt()); setPadding(0, 8, 0, 0)
        })
        layout.addView(android.widget.TextView(this).apply {
            text = "\n当前配置示例："; textSize = 12f; setTextColor(0xFF6750A4.toInt())
        })
        layout.addView(android.widget.TextView(this).apply {
            id = android.R.id.text1; textSize = 11f; setTextColor(0xFF49454F.toInt())
        })

        AlertDialog.Builder(this)
            .setTitle("Shizuku 答题配置")
            .setView(layout)
            .setPositiveButton("保存") { _, _ ->
                val total = countInput.text.toString().toIntOrNull()?.coerceIn(1, 200) ?: 10
                val minTime = timeInput.text.toString().toIntOrNull()?.coerceIn(10, 600) ?: 30
                AutoAnswerConfig.save(this, AutoAnswerConfig(totalQuestions = total, minTotalTimeSec = minTime))
                Toast.makeText(this, "配置已保存", Toast.LENGTH_SHORT).show()
                updateShizukuConfigButton()
            }
            .setNegativeButton("取消", null)
            .show()

        val previewText = layout.findViewById<android.widget.TextView>(android.R.id.text1)
        fun updatePreview() {
            val t = countInput.text.toString().toIntOrNull()?.coerceIn(1, 200) ?: 10
            val mt = timeInput.text.toString().toIntOrNull()?.coerceIn(10, 600) ?: 30
            val times = AutoAnswerConfig(totalQuestions = t, minTotalTimeSec = mt).generateClickTimes()
            val totalSec = times.sum() / 1000.0
            previewText.text = "每题时间（ms）：${times.take(5).joinToString(", ")}${if (times.size > 5) "..." else ""}\n总时间：${"%.1f".format(totalSec)} 秒（要求 ≥ ${mt + 10} 秒）"
        }
        countInput.addTextChangedListener(updateWatcher { updatePreview() })
        timeInput.addTextChangedListener(updateWatcher { updatePreview() })
        updatePreview()
    }

    private fun updateWatcher(action: () -> Unit) = object : android.text.TextWatcher {
        override fun afterTextChanged(s: android.text.Editable?) = action()
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private fun updateShizukuConfigButton() {
        val config = AutoAnswerConfig.load(this)
        binding.btnShizukuConfig.text = "答题配置：共 ${config.totalQuestions} 题，最低 ${config.minTotalTimeSec} 秒"
    }

    // ==================== 检测更新 ====================

    private fun checkForUpdate() {
        binding.tvUpdateStatus.text = "正在检查更新..."
        binding.btnCheckUpdate.isEnabled = false
        lifecycleScope.launch {
            try {
                val info = withContext(Dispatchers.IO) {
                    UpdateChecker.checkUpdate("1.2.0")
                }
                if (info != null) {
                    showUpdateFoundDialog(info)
                } else {
                    binding.tvUpdateStatus.text = "✓ 当前已是最新版本"
                    binding.tvUpdateStatus.setTextColor(0xFF4CAF50.toInt())
                }
            } catch (_: Exception) {
                binding.tvUpdateStatus.text = "检查失败，请检查网络连接"
                binding.tvUpdateStatus.setTextColor(0xFFF44336.toInt())
            } finally {
                binding.btnCheckUpdate.isEnabled = true
            }
        }
    }

    private fun showUpdateFoundDialog(info: UpdateChecker.UpdateInfo) {
        AlertDialog.Builder(this)
            .setTitle("发现新版本 v${info.latestVersion}")
            .setMessage("当前版本：v1.2.0\n\n${info.releaseNotes.take(500)}")
            .setPositiveButton("立即下载") { _, _ -> showMirrorSelectorDialog(info) }
            .setNegativeButton("稍后", null)
            .show()
    }

    private fun showMirrorSelectorDialog(info: UpdateChecker.UpdateInfo) {
        val mirrorNames = UpdateChecker.mirrors.map { it.name }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("选择下载方式")
            .setItems(mirrorNames) { _, which ->
                val prefix = if (which == 0) "" else UpdateChecker.mirrors[which].prefix
                val url = "${prefix}https://github.com/FoolishWiser/QuizSnap/releases/download/v${info.latestVersion}/app-debug.apk"
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                binding.tvUpdateStatus.text = "正在跳转下载 v${info.latestVersion}..."
                binding.tvUpdateStatus.setTextColor(0xFF4CAF50.toInt())
            }
            .setNegativeButton("取消", null)
            .show()
    }

    // ==================== 状态 ====================

    private fun updateStatusText() {
        val a11yOk = isAccessibilityServiceEnabled()
        val a11yEnabled = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getBoolean(KEY_AUTO_ANSWER, false)
        val shizukuEnabled = AutoAnswerConfig.isShizukuEnabled(this)

        binding.tvStatus.text = when {
            a11yOk && a11yEnabled -> "✓ 无障碍自动答题已启用"
            a11yOk && !a11yEnabled -> "○ 无障碍权限已开启，功能未启用"
            else -> "× 无障碍权限未开启"
        }

        if (shizukuEnabled && ShizukuAnswerService.isInitialized) {
            binding.tvShizukuStatus.text = "✓ Shizuku 模式已就绪"
            binding.tvShizukuStatus.setTextColor(0xFF4CAF50.toInt())
            binding.btnShizukuConfig.isEnabled = true
        } else if (shizukuEnabled) {
            binding.tvShizukuStatus.text = "○ Shizuku 正在连接..."
            binding.tvShizukuStatus.setTextColor(0xFFFF9800.toInt())
            binding.btnShizukuConfig.isEnabled = true
        } else {
            binding.tvShizukuStatus.text = "× Shizuku 未启用"
            binding.tvShizukuStatus.setTextColor(0xFFF44336.toInt())
            binding.btnShizukuConfig.isEnabled = false
        }
    }

    override fun onResume() {
        super.onResume()
        updateStatusText()
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val shouldBeEnabled = prefs.getBoolean(KEY_AUTO_ANSWER, false)
        if (shouldBeEnabled && !isAccessibilityServiceEnabled()) {
            binding.switchAutoAnswer.isChecked = false
            savePreference(false)
            AutoAnswerService.isEnabled = false
        }
    }
}
