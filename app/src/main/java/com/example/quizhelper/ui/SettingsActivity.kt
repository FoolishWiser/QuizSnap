package com.example.quizhelper.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizhelper.R
import com.example.quizhelper.databinding.ActivitySettingsBinding

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
        // 返回按钮
        binding.btnBack.setOnClickListener {
            finish()
        }

        // 加载当前设置状态
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        binding.switchAutoAnswer.isChecked = prefs.getBoolean(KEY_AUTO_ANSWER, false)

        // 自动答题开关
        binding.switchAutoAnswer.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // 检查无障碍权限
                if (!isAccessibilityServiceEnabled()) {
                    showAccessibilityDialog()
                } else {
                    savePreference(isChecked)
                    Toast.makeText(this, "自动答题已开启", Toast.LENGTH_SHORT).show()
                }
            } else {
                savePreference(isChecked)
                Toast.makeText(this, "自动答题已关闭", Toast.LENGTH_SHORT).show()
            }
        }

        // 状态说明
        updateStatusText()
    }

    private fun savePreference(enabled: Boolean) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_AUTO_ANSWER, enabled)
            .apply()
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val serviceName = "${packageName}/.service.AutoAnswerService"
        val enabledServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        return enabledServices?.contains(serviceName) == true
    }

    private fun showAccessibilityDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("需要无障碍权限")
            .setMessage("自动答题功能需要开启无障碍权限。开启后，应用将能够：\n\n1. 识别屏幕上的题目内容\n2. 自动点击匹配的答案选项\n3. 自动进入下一题\n\n请放心，应用不会收集或上传您的任何个人信息。")
            .setPositiveButton("去设置") { _, _ ->
                openAccessibilitySettings()
            }
            .setNegativeButton("取消") { _, _ ->
                binding.switchAutoAnswer.isChecked = false
            }
            .setOnCancelListener {
                binding.switchAutoAnswer.isChecked = false
            }
            .show()
    }

    private fun openAccessibilitySettings() {
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
            Toast.makeText(this, "请找到「自动答题服务」并开启", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "无法打开设置: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateStatusText() {
        val accessibilityEnabled = isAccessibilityServiceEnabled()
        val autoAnswerEnabled = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .getBoolean(KEY_AUTO_ANSWER, false)

        binding.tvStatus.text = when {
            accessibilityEnabled && autoAnswerEnabled -> "✓ 自动答题已启用"
            accessibilityEnabled && !autoAnswerEnabled -> "○ 无障碍权限已开启，功能未启用"
            else -> "× 无障碍权限未开启"
        }
    }

    override fun onResume() {
        super.onResume()
        // 每次返回页面时更新状态
        updateStatusText()
        
        // 检查权限状态是否改变
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val shouldBeEnabled = prefs.getBoolean(KEY_AUTO_ANSWER, false)
        if (shouldBeEnabled && !isAccessibilityServiceEnabled()) {
            binding.switchAutoAnswer.isChecked = false
            savePreference(false)
        }
    }
}
