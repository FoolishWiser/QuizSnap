package com.example.quizhelper.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.pm.PackageManager
import android.graphics.Path
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.quizhelper.R
import com.example.quizhelper.data.AppDatabase
import com.example.quizhelper.model.Question
import com.example.quizhelper.utils.QuestionMatcher
import kotlinx.coroutines.*
import java.util.concurrent.CopyOnWriteArrayList

class AutoAnswerService : AccessibilityService() {

    companion object {
        private const val TAG = "AutoAnswerService"
        
        // 全局实例，供外部调用
        var instance: AutoAnswerService? = null
            private set
        
        // 是否启用自动答题
        @Volatile
        var isEnabled = false
        
        // 题库缓存
        val questionCache = CopyOnWriteArrayList<Question>()
        
        // 是否正在处理
        @Volatile
        var isProcessing = false
        
        // 上一次处理的题目内容（避免重复处理）
        var lastProcessedContent: String? = null
        
        // 匹配成功后等待时间（毫秒）
        const val MATCH_SUCCESS_DELAY = 1500L
        const val CLICK_DELAY = 800L
        const val NEXT_BUTTON_DELAY = 1200L
    }

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private val mainHandler = Handler(Looper.getMainLooper())
    private lateinit var database: AppDatabase
    
    // 存储找到的选项节点
    private val optionNodes = mutableListOf<AccessibilityNodeInfo>()
    private var nextButtonNode: AccessibilityNodeInfo? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = AppDatabase.getDatabase(this)
        loadQuestions()
        Log.d(TAG, "AutoAnswerService created")
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        isEnabled = false
        serviceScope.cancel()
        Log.d(TAG, "AutoAnswerService destroyed")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "AutoAnswerService connected")
        Toast.makeText(this, "自动答题服务已开启", Toast.LENGTH_SHORT).show()
    }

    private fun loadQuestions() {
        serviceScope.launch {
            try {
                val questions = withContext(Dispatchers.IO) {
                    database.questionDao().getAllQuestionsList()
                }
                questionCache.clear()
                questionCache.addAll(questions)
                Log.d(TAG, "Loaded ${questions.size} questions into cache")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load questions: ${e.message}")
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (!isEnabled || isProcessing) return
        
        event ?: return
        
        // 只关注窗口内容变化事件
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED,
            AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                // 延迟处理，等待界面稳定
                mainHandler.postDelayed({
                    processCurrentScreen()
                }, 500)
            }
        }
    }

    private fun processCurrentScreen() {
        if (!isEnabled || isProcessing) return
        if (questionCache.isEmpty()) {
            Log.d(TAG, "Question cache is empty")
            return
        }

        isProcessing = true
        
        try {
            val rootNode = rootInActiveWindow ?: run {
                isProcessing = false
                return
            }

            // 提取题目内容
            val questionContent = extractQuestionContent(rootNode)
            if (questionContent.isNullOrEmpty()) {
                Log.d(TAG, "No question content found")
                isProcessing = false
                return
            }

            // 检查是否与上次处理的相同
            if (questionContent == lastProcessedContent) {
                Log.d(TAG, "Same question as last processed, skipping")
                isProcessing = false
                return
            }

            Log.d(TAG, "Processing question: $questionContent")

            // 在题库中匹配
            val matchedQuestion = QuestionMatcher.matchQuestion(questionContent, questionCache.toList())
            
            if (matchedQuestion != null) {
                Log.d(TAG, "Found match: ${matchedQuestion.content}")
                lastProcessedContent = questionContent
                
                // 显示找到的答案
                val answerText = QuestionMatcher.getAnswerDisplayText(matchedQuestion)
                Toast.makeText(this, "找到答案: $answerText", Toast.LENGTH_SHORT).show()
                
                // 找到并点击对应选项
                findAndClickOption(rootNode, matchedQuestion)
            } else {
                Log.d(TAG, "No match found for question")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error processing screen: ${e.message}")
        } finally {
            isProcessing = false
        }
    }

    /**
     * 从界面中提取题目内容
     */
    private fun extractQuestionContent(rootNode: AccessibilityNodeInfo): String? {
        val textBuilder = StringBuilder()
        
        // 遍历所有节点，查找题目相关内容
        findQuestionText(rootNode, textBuilder)
        
        return if (textBuilder.isNotEmpty()) textBuilder.toString().trim() else null
    }

    private fun findQuestionText(node: AccessibilityNodeInfo, textBuilder: StringBuilder) {
        try {
            val text = node.text?.toString()
            val contentDesc = node.contentDescription?.toString()
            
            // 跳过选项类文字（太短的通常是选项）
            if (!text.isNullOrEmpty() && text.length > 10) {
                textBuilder.append(text).append(" ")
            }
            if (!contentDesc.isNullOrEmpty() && contentDesc.length > 10) {
                textBuilder.append(contentDesc).append(" ")
            }
            
            // 递归遍历子节点
            for (i in 0 until node.childCount) {
                val child = node.getChild(i)
                if (child != null) {
                    findQuestionText(child, textBuilder)
                    child.recycle()
                }
            }
        } catch (e: Exception) {
            // 忽略异常
        }
    }

    /**
     * 查找并点击正确答案选项
     */
    private fun findAndClickOption(rootNode: AccessibilityNodeInfo, question: Question) {
        // 清除之前的选项节点
        optionNodes.clear()
        
        // 收集所有可能是选项的节点
        collectOptionNodes(rootNode)
        
        if (optionNodes.isEmpty()) {
            Log.d(TAG, "No option nodes found")
            return
        }

        val answerText = QuestionMatcher.getAnswerDisplayText(question)
        Log.d(TAG, "Looking for answer: $answerText")
        
        // 根据题目类型确定要点击的选项
        val targetOption = when (question.type) {
            "single" -> {
                // 单选题：直接点击包含答案文字的选项
                findOptionByText(answerText)
            }
            "multiple" -> {
                // 多选题：依次点击各答案
                val answers = answerText.split(",").map { it.trim() }
                answers.forEach { ans ->
                    findOptionByText(ans)
                }
                null
            }
            "judgment" -> {
                // 判断题：根据答案点击"对"或"错"
                findOptionByText(answerText)
            }
            else -> null
        }
        
        if (targetOption != null) {
            // 执行点击
            mainHandler.postDelayed({
                clickOnNode(targetOption)
            }, CLICK_DELAY)
        }
    }

    /**
     * 收集所有可能是选项的节点
     */
    private fun collectOptionNodes(node: AccessibilityNodeInfo) {
        try {
            val text = node.text?.toString()
            val isClickable = node.isClickable
            val isEnabled = node.isEnabled
            
            // 如果是可点击的节点，且文字不是太长（排除题目），且不是按钮类
            if (isClickable && isEnabled && !text.isNullOrEmpty()) {
                val className = node.className?.toString() ?: ""
                // 排除Next、提交、确定等按钮
                if (!text.contains("下一", ignoreCase = true) &&
                    !text.contains("提交", ignoreCase = true) &&
                    !text.contains("确定", ignoreCase = true) &&
                    !text.contains("next", ignoreCase = true) &&
                    !text.contains("submit", ignoreCase = true)) {
                    optionNodes.add(node)
                }
            }
            
            // 递归遍历
            for (i in 0 until node.childCount) {
                val child = node.getChild(i)
                if (child != null) {
                    collectOptionNodes(child)
                    child.recycle()
                }
            }
        } catch (e: Exception) {
            // 忽略
        }
    }

    /**
     * 根据答案文字查找对应的选项节点
     */
    private fun findOptionByText(answerText: String): AccessibilityNodeInfo? {
        // 清理答案文字
        val cleanAnswer = answerText.trim()
        
        for (node in optionNodes) {
            try {
                val nodeText = node.text?.toString() ?: continue
                val contentDesc = node.contentDescription?.toString() ?: ""
                
                // 匹配：节点文字包含答案，或答案包含节点文字
                if (nodeText.contains(cleanAnswer, ignoreCase = true) ||
                    cleanAnswer.contains(nodeText, ignoreCase = true) ||
                    contentDesc.contains(cleanAnswer, ignoreCase = true)) {
                    return node
                }
                
                // 如果答案是选项标号（A/B/C/D）
                val optionIndex = when {
                    cleanAnswer.equals("A", ignoreCase = true) -> 0
                    cleanAnswer.equals("B", ignoreCase = true) -> 1
                    cleanAnswer.equals("C", ignoreCase = true) -> 2
                    cleanAnswer.equals("D", ignoreCase = true) -> 3
                    else -> -1
                }
                
                if (optionIndex >= 0 && optionIndex < optionNodes.size) {
                    return optionNodes[optionIndex]
                }
                
            } catch (e: Exception) {
                continue
            }
        }
        
        // 如果没找到精确匹配，尝试模糊匹配
        for (node in optionNodes) {
            try {
                val nodeText = node.text?.toString() ?: continue
                // 取节点文字的前几个字符匹配
                if (nodeText.length >= 2 && cleanAnswer.startsWith(nodeText.take(2))) {
                    return node
                }
            } catch (e: Exception) {
                continue
            }
        }
        
        return null
    }

    /**
     * 在指定节点上执行点击
     */
    private fun clickOnNode(node: AccessibilityNodeInfo) {
        try {
            val bounds = Rect()
            node.getBoundsInScreen(bounds)
            
            // 计算点击位置（节点中心）
            val x = bounds.centerX().toFloat()
            val y = bounds.centerY().toFloat()
            
            Log.d(TAG, "Clicking at ($x, $y)")
            
            // 使用手势描述执行点击
            val path = Path().apply {
                moveTo(x, y)
            }
            
            val gesture = GestureDescription.Builder()
                .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
                .build()
            
            val result = dispatchGesture(gesture, object : AccessibilityService.GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    Log.d(TAG, "Click completed")
                    // 点击完成后，自动点击下一题按钮
                    mainHandler.postDelayed({
                        clickNextButton()
                    }, NEXT_BUTTON_DELAY)
                }
                
                override fun onCancelled(gestureDescription: GestureDescription?) {
                    Log.d(TAG, "Click cancelled")
                }
            }, null)
            
            if (!result) {
                Log.e(TAG, "Failed to dispatch gesture")
                // 尝试使用performAction
                if (node.isClickable) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    mainHandler.postDelayed({
                        clickNextButton()
                    }, NEXT_BUTTON_DELAY)
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error clicking node: ${e.message}")
        }
    }

    /**
     * 查找并点击下一题/提交按钮
     */
    private fun clickNextButton() {
        if (!isEnabled) return
        
        try {
            val rootNode = rootInActiveWindow ?: return
            
            // 查找下一题按钮
            var foundNext = false
            findAndClickNextButton(rootNode, foundNext)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error finding next button: ${e.message}")
        }
    }

    private fun findAndClickNextButton(node: AccessibilityNodeInfo, foundNext: Boolean): Boolean {
        if (foundNext) return true
        
        try {
            val text = node.text?.toString() ?: ""
            val contentDesc = node.contentDescription?.toString() ?: ""
            val isClickable = node.isClickable
            val isEnabled = node.isEnabled
            
            // 识别下一题/提交按钮
            val isNextButton = text.contains("下一", ignoreCase = true) ||
                    text.contains("下一题", ignoreCase = true) ||
                    text.contains("提交", ignoreCase = true) ||
                    text.contains("确定", ignoreCase = true) ||
                    text.contains("next", ignoreCase = true) ||
                    text.contains("submit", ignoreCase = true) ||
                    contentDesc.contains("下一", ignoreCase = true) ||
                    contentDesc.contains("next", ignoreCase = true)
            
            if (isClickable && isEnabled && isNextButton) {
                Log.d(TAG, "Found next button: $text")
                
                // 执行点击
                val bounds = Rect()
                node.getBoundsInScreen(bounds)
                val x = bounds.centerX().toFloat()
                val y = bounds.centerY().toFloat()
                
                val path = Path().apply {
                    moveTo(x, y)
                }
                
                val gesture = GestureDescription.Builder()
                    .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
                    .build()
                
                dispatchGesture(gesture, null, null)
                return true
            }
            
            // 递归查找
            for (i in 0 until node.childCount) {
                val child = node.getChild(i)
                if (child != null) {
                    if (findAndClickNextButton(child, foundNext)) {
                        child.recycle()
                        return true
                    }
                    child.recycle()
                }
            }
        } catch (e: Exception) {
            // 忽略
        }
        
        return false
    }

    override fun onInterrupt() {
        Log.d(TAG, "Service interrupted")
    }
    
    /**
     * 手动触发一次答题处理
     */
    fun triggerAnswer() {
        mainHandler.postDelayed({
            processCurrentScreen()
        }, 300)
    }
    
    /**
     * 刷新题库缓存
     */
    fun refreshCache() {
        loadQuestions()
    }
}
