package com.example.quizhelper.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Path
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import com.example.quizhelper.data.AppDatabase
import com.example.quizhelper.model.Question
import com.example.quizhelper.utils.QuestionMatcher
import kotlinx.coroutines.*
import java.util.concurrent.CopyOnWriteArrayList

class AutoAnswerService : AccessibilityService() {

    companion object {
        private const val TAG = "AutoAnswerService"

        var instance: AutoAnswerService? = null
            private set

        @Volatile
        var isEnabled = false

        val questionCache = CopyOnWriteArrayList<Question>()

        @Volatile
        var isProcessing = false

        var lastProcessedContent: String? = null

        const val CLICK_DELAY = 800L
        const val NEXT_BUTTON_DELAY = 1200L
    }

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private val mainHandler = Handler(Looper.getMainLooper())
    private lateinit var database: AppDatabase
    private lateinit var prefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = AppDatabase.getDatabase(this)
        prefs = getSharedPreferences("quiz_helper_prefs", Context.MODE_PRIVATE)
        isEnabled = prefs.getBoolean("auto_answer_enabled", false)
        loadQuestions()
        Log.d(TAG, "AutoAnswerService created, isEnabled=$isEnabled")
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
        isEnabled = prefs.getBoolean("auto_answer_enabled", false)
        Log.d(TAG, "AutoAnswerService connected, isEnabled=$isEnabled")
        if (isEnabled) {
            Toast.makeText(this, "自动答题服务已开启", Toast.LENGTH_SHORT).show()
        }
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

        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED,
            AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                mainHandler.postDelayed({ processCurrentScreen() }, 500)
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

            val questionContent = extractQuestionContent(rootNode)
            if (questionContent.isNullOrEmpty()) {
                Log.d(TAG, "No question content found")
                isProcessing = false
                return
            }

            if (questionContent == lastProcessedContent) {
                Log.d(TAG, "Same question as last processed, skipping")
                isProcessing = false
                return
            }

            Log.d(TAG, "Processing question: $questionContent")

            val matchedQuestion = QuestionMatcher.matchQuestion(questionContent, questionCache.toList())
            if (matchedQuestion != null) {
                Log.d(TAG, "Found match: ${matchedQuestion.content}")
                lastProcessedContent = questionContent

                val answerText = QuestionMatcher.getAnswerDisplayText(matchedQuestion)
                Toast.makeText(this, "找到答案: $answerText", Toast.LENGTH_SHORT).show()
                findAndClickOption(matchedQuestion)
            } else {
                Log.d(TAG, "No match found")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing screen: ${e.message}")
        } finally {
            isProcessing = false
        }
    }

    private fun extractQuestionContent(rootNode: AccessibilityNodeInfo): String? {
        val textBuilder = StringBuilder()
        findQuestionText(rootNode, textBuilder)
        return if (textBuilder.isNotEmpty()) textBuilder.toString().trim() else null
    }

    private fun findQuestionText(node: AccessibilityNodeInfo, textBuilder: StringBuilder) {
        try {
            val text = node.text?.toString()
            val contentDesc = node.contentDescription?.toString()

            if (!text.isNullOrEmpty() && text.length > 8) {
                textBuilder.append(text).append(" ")
            }
            if (!contentDesc.isNullOrEmpty() && contentDesc.length > 8) {
                textBuilder.append(contentDesc).append(" ")
            }

            for (i in 0 until node.childCount) {
                val child = node.getChild(i)
                if (child != null) {
                    findQuestionText(child, textBuilder)
                    child.recycle()
                }
            }
        } catch (_: Exception) {}
    }

    /** 查找并点击正确答案 */
    private fun findAndClickOption(question: Question) {
        val answerText = QuestionMatcher.getAnswerDisplayText(question)
        Log.d(TAG, "Looking for answer: $answerText")

        // 根据题目类型确定策略
        when (question.type) {
            "judgment" -> {
                // 判断题：找"对"/"错"的文字节点
                clickNodeByText(answerText)
            }
            else -> {
                // 单选/多选：先按编号（A/B/C/D）找，再按文字匹配
                val answerIndex = when (answerText) {
                    "A" -> 0; "B" -> 1; "C" -> 2; "D" -> 3; else -> -1
                }
                if (answerIndex >= 0) {
                    clickNodeByIndex(answerIndex)
                } else {
                    clickNodeByText(answerText)
                }
            }
        }
    }

    /** 按索引点击选项（根据屏幕上的可选项列表） */
    private fun clickNodeByIndex(index: Int) {
        val rootNode = rootInActiveWindow ?: return
        val candidates = mutableListOf<AccessibilityNodeInfo>()
        collectClickableNodes(rootNode, candidates)

        // 过滤出选项节点（去掉按钮、标题等）
        val optionNodes = candidates.filter { n ->
            val t = n.text?.toString() ?: ""
            t.length < 15 && !t.contains("下一") && !t.contains("提交") && !t.contains("确定")
        }

        if (index < optionNodes.size) {
            performClick(optionNodes[index])
        } else if (candidates.isNotEmpty()) {
            // 兜底：如果序号溢出，点最后一个候选
            performClick(candidates.last())
        }
    }

    /** 按文字搜索并点击 */
    private fun clickNodeByText(target: String) {
        val rootNode = rootInActiveWindow ?: return
        val cleaned = target.trim()
        val node = findNodeByText(rootNode, cleaned)
        if (node != null) {
            performClick(node)
            return
        }
        Log.d(TAG, "Text node not found: $cleaned")
    }

    /** 递归收集所有可点击节点 */
    private fun collectClickableNodes(node: AccessibilityNodeInfo, out: MutableList<AccessibilityNodeInfo>) {
        try {
            if (node.isClickable && node.isEnabled) {
                out.add(node)
            }
            for (i in 0 until node.childCount) {
                val child = node.getChild(i)
                if (child != null) {
                    collectClickableNodes(child, out)
                    child.recycle()
                }
            }
        } catch (_: Exception) {}
    }

    /** 按文字递归搜索节点 */
    private fun findNodeByText(node: AccessibilityNodeInfo, text: String): AccessibilityNodeInfo? {
        try {
            val nodeText = node.text?.toString() ?: ""
            val desc = node.contentDescription?.toString() ?: ""
            if (nodeText.contains(text, ignoreCase = true) || desc.contains(text, ignoreCase = true)) {
                return node
            }
            for (i in 0 until node.childCount) {
                val child = node.getChild(i)
                if (child != null) {
                    val result = findNodeByText(child, text)
                    if (result != null) { child.recycle(); return result }
                    child.recycle()
                }
            }
        } catch (_: Exception) {}
        return null
    }

    /** 执行点击 */
    private fun performClick(node: AccessibilityNodeInfo) {
        try {
            if (node.isClickable) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                Log.d(TAG, "Clicked via ACTION_CLICK")
            } else {
                val bounds = Rect()
                node.getBoundsInScreen(bounds)
                val x = bounds.centerX().toFloat()
                val y = bounds.centerY().toFloat()
                val path = Path().apply { moveTo(x, y) }
                val gesture = GestureDescription.Builder()
                    .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
                    .build()
                dispatchGesture(gesture, null, null)
                Log.d(TAG, "Clicked via gesture at ($x, $y)")
            }

            // 点击完成后自动找下一题
            mainHandler.postDelayed({ clickNextButton() }, NEXT_BUTTON_DELAY)
        } catch (e: Exception) {
            Log.e(TAG, "Click error: ${e.message}")
        }
    }

    /** 查找并点击下一题按钮 */
    private fun clickNextButton() {
        if (!isEnabled) return
        try {
            val rootNode = rootInActiveWindow ?: return
            if (findAndClickNextButton(rootNode)) {
                Log.d(TAG, "Clicked next button")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error clicking next: ${e.message}")
        }
    }

    /** 递归查找下一题按钮（修复：正确使用返回值） */
    private fun findAndClickNextButton(node: AccessibilityNodeInfo): Boolean {
        try {
            val text = node.text?.toString() ?: ""
            val desc = node.contentDescription?.toString() ?: ""
            val isNext = text.contains("下一") || text.contains("提交") || text.contains("确定") ||
                    text.contains("next", ignoreCase = true) || text.contains("submit", ignoreCase = true) ||
                    desc.contains("下一") || desc.contains("next", ignoreCase = true)

            if (node.isClickable && node.isEnabled && isNext) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                return true
            }

            for (i in 0 until node.childCount) {
                val child = node.getChild(i)
                if (child != null) {
                    if (findAndClickNextButton(child)) {
                        child.recycle()
                        return true
                    }
                    child.recycle()
                }
            }
        } catch (_: Exception) {}
        return false
    }

    override fun onInterrupt() {
        Log.d(TAG, "Service interrupted")
    }

    fun triggerAnswer() {
        mainHandler.postDelayed({ processCurrentScreen() }, 300)
    }

    fun refreshCache() {
        loadQuestions()
    }
}
