package com.example.quizhelper.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.MediaStore
import android.provider.Settings
import android.view.*
import android.widget.*
import androidx.lifecycle.lifecycleScope
import com.example.quizhelper.R
import com.example.quizhelper.data.AppDatabase
import com.example.quizhelper.model.Question
import com.example.quizhelper.utils.OCRHelper
import com.example.quizhelper.utils.QuestionMatcher
import kotlinx.coroutines.*
import java.io.IOException

class FloatingWindowService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View
    private lateinit var answerView: View
    private lateinit var database: AppDatabase
    
    private var initialX: Int = 0
    private var initialY: Int = 0
    private var initialTouchX: Float = 0f
    private var initialTouchY: Float = 0f
    private var isAnswerVisible = false
    private var allQuestions: List<Question> = emptyList()
    
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getDatabase(this)
        setupFloatingWindow()
        loadQuestions()
    }

    private fun setupFloatingWindow() {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        // 悬浮按钮布局
        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_button, null)

        // 答案显示布局
        answerView = LayoutInflater.from(this).inflate(R.layout.floating_answer, null)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START
        params.x = 0
        params.y = 100

        // 悬浮按钮事件
        val btnStart = floatingView.findViewById<Button>(R.id.btnStart)
        btnStart.setOnClickListener {
            // 检查自动答题服务是否启用
            if (AutoAnswerService.isEnabled) {
                // 直接触发自动答题
                AutoAnswerService.instance?.triggerAnswer()
                showToast("正在自动答题...")
            } else {
                // 使用原来的方式（手动输入搜索）
                startOCR()
            }
        }

        val btnAuto = floatingView.findViewById<Button>(R.id.btnAuto)
        btnAuto.setOnClickListener {
            // 打开设置页面开启自动答题
            showToast("请先在设置中开启自动答题功能")
            startActivity(android.content.Intent(this, com.example.quizhelper.ui.SettingsActivity::class.java))
        }

        val btnClose = floatingView.findViewById<Button>(R.id.btnClose)
        btnClose.setOnClickListener {
            stopSelf()
        }

        // 拖动功能
        floatingView.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (event.rawX - initialTouchX).toInt()
                    params.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager.updateViewLayout(floatingView, params)
                    true
                }
                else -> false
            }
        }

        windowManager.addView(floatingView, params)

        // 答案窗口参数
        val answerParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        answerParams.gravity = Gravity.BOTTOM
        answerParams.y = 100

        // 答案窗口关闭按钮
        val btnCloseAnswer = answerView.findViewById<Button>(R.id.btnCloseAnswer)
        btnCloseAnswer.setOnClickListener {
            hideAnswer()
        }

        // 暂不添加答案窗口，等到需要时再添加
    }

    private fun loadQuestions() {
        serviceScope.launch {
            allQuestions = withContext(Dispatchers.IO) {
                database.questionDao().searchByContent("")
            }
        }
    }

    private fun startOCR() {
        serviceScope.launch {
            try {
                // 显示"识别中"提示
                showToast("正在识别屏幕内容...")

                // 截屏并识别
                val bitmap = captureScreen()
                if (bitmap != null) {
                    val text = OCRHelper.recognizeText(bitmap)

                    // 在题库中匹配
                    val question = QuestionMatcher.matchQuestion(text, allQuestions)

                    if (question != null) {
                        showAnswer(question)
                    } else {
                        showToast("未找到匹配的题目")
                    }
                } else {
                    showToast("截屏失败")
                }
            } catch (e: Exception) {
                showToast("识别失败: ${e.message}")
            }
        }
    }

    private suspend fun captureScreen(): android.graphics.Bitmap? = withContext(Dispatchers.IO) {
        // 由于MediaProjection权限复杂，提供两种方案：
        // 方案1：让用户手动输入题目进行搜索
        // 方案2：从剪贴板读取图片（如果支持）
        
        // 这里实现方案1：弹出输入框让用户输入题目
        showInputDialog()
        null
    }
    
    private fun showInputDialog() {
        // 在主线程显示输入对话框
        serviceScope.launch(Dispatchers.Main) {
            val editText = android.widget.EditText(this@FloatingWindowService).apply {
                hint = "请输入题目关键词"
            }
            
            androidx.appcompat.app.AlertDialog.Builder(this@FloatingWindowService)
                .setTitle("输入题目")
                .setView(editText)
                .setPositiveButton("搜索") { _, _ ->
                    val keyword = editText.text.toString()
                    if (keyword.isNotEmpty()) {
                        searchQuestion(keyword)
                    }
                }
                .setNegativeButton("取消", null)
                .show()
        }
    }
    
    private fun searchQuestion(keyword: String) {
        serviceScope.launch {
            val results = withContext(Dispatchers.IO) {
                database.questionDao().searchByContent(keyword)
            }
            
            if (results.isNotEmpty()) {
                // 显示第一个匹配的结果
                showAnswer(results[0])
            } else {
                showToast("未找到匹配的题目")
            }
        }
    }

    private fun showAnswer(question: Question) {
        if (!isAnswerVisible) {
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else
                    WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
            
            params.gravity = Gravity.CENTER
            
            // 设置答案内容
            val tvType = answerView.findViewById<TextView>(R.id.tvAnswerType)
            val tvContent = answerView.findViewById<TextView>(R.id.tvAnswerContent)
            val tvAnswer = answerView.findViewById<TextView>(R.id.tvAnswerText)
            
            tvType.text = QuestionMatcher.getTypeDisplayText(question.type)
            tvContent.text = question.content
            tvAnswer.text = "答案: ${QuestionMatcher.getAnswerDisplayText(question)}"
            
            windowManager.addView(answerView, params)
            isAnswerVisible = true
        }
    }

    private fun hideAnswer() {
        if (isAnswerVisible) {
            windowManager.removeView(answerView)
            isAnswerVisible = false
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        if (::floatingView.isInitialized && ::windowManager.isInitialized) {
            windowManager.removeView(floatingView)
        }
        if (isAnswerVisible && ::answerView.isInitialized && ::windowManager.isInitialized) {
            windowManager.removeView(answerView)
        }
        serviceScope.cancel()
    }
}
