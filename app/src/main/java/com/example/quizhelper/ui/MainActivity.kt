package com.example.quizhelper.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizhelper.R
import com.example.quizhelper.data.AppDatabase
import com.example.quizhelper.databinding.ActivityMainBinding
import com.example.quizhelper.model.Question
import com.example.quizhelper.utils.ExcelParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: AppDatabase
    private lateinit var questionAdapter: QuestionAdapter
    
    // 文件选择器
    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                readFileAndParse(uri)
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        database = AppDatabase.getDatabase(this)
        
        setupUI()
        observeQuestions()
    }
    
    private fun setupUI() {
        // 设置AI提示词
        binding.tvAiPrompt.text = getAiPrompt()
        
        // 复制提示词按钮
        binding.btnCopyPrompt.setOnClickListener {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("AI提示词", getAiPrompt())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "提示词已复制到剪贴板", Toast.LENGTH_SHORT).show()
        }
        
        // 手动输入按钮
        binding.btnInputText.setOnClickListener {
            showTextInputDialog()
        }
        
        // 获取剪贴板按钮
        binding.btnPasteClipboard.setOnClickListener {
            showClipboardConfirmDialog()
        }
        
        // 查看历史记录按钮
        binding.btnViewHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
        
        // 启动悬浮窗按钮
        binding.btnStartFloating.setOnClickListener {
            startFloatingWindow()
        }
        
        // 设置按钮
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        
        // 设置RecyclerView - 使用Flow响应式更新，无需外部数据列表
        questionAdapter = QuestionAdapter { question ->
            showEditQuestionDialog(question)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = questionAdapter
    }
    
    // 使用 Flow 响应式监听数据库变化，自动更新列表
    private fun observeQuestions() {
        lifecycleScope.launch {
            database.questionDao().getRecentQuestions().collectLatest { list ->
                questionAdapter.submitList(list)
            }
        }
    }
    
    private fun getAiPrompt(): String {
        return """
            请将以下题库内容（支持Excel/PDF格式）转换为统一格式，每行一个题目，格式如下：
            
            题目类型|题目内容|选项A|选项B|选项C|选项D|正确答案
            
            说明：
            1. 题目类型：单选、多选或判断
            2. 题目内容：完整的题目描述
            3. 选项：A、B、C、D四个选项的内容（判断题型无需选项）
            4. 正确答案：单选填A/B/C/D，多选填A,B,C（逗号为英文逗号），判断填对/错
            
            示例：
            单选|以下哪个是动物？|猫|桌子|书|电脑|A
            多选|以下哪些是水果？|苹果|香蕉|胡萝卜|西瓜|A,B,D
            判断|太阳从东边升起|对|错|||对
            
            请严格按照此格式输出，确保分隔符为竖线|，不包含表头。
        """.trimIndent()
    }
    
    private fun selectFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            val mimeTypes = arrayOf(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/pdf"
            )
            putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        filePickerLauncher.launch(intent)
    }
    
    private fun readFileAndParse(uri: Uri) {
        lifecycleScope.launch {
            try {
                val fileName = getFileName(uri)
                val isPdf = fileName.endsWith(".pdf", true)
                val isExcel = fileName.endsWith(".xlsx", true) || fileName.endsWith(".xls", true)

                if (isExcel) {
                    // 解析Excel - 使用 use 块自动关闭 InputStream
                    contentResolver.openInputStream(uri)?.use { inputStream ->
                        val parsedQuestions = withContext(Dispatchers.IO) {
                            ExcelParser.parseExcel(inputStream, fileName)
                        }
                        if (parsedQuestions.isEmpty()) {
                            Toast.makeText(this@MainActivity, "Excel文件中未解析到有效题目", Toast.LENGTH_SHORT).show()
                        } else {
                            // 先显示命名对话框，再保存
                            saveWithQuizNameDialog(parsedQuestions, fileName)
                        }
                    } ?: run {
                        Toast.makeText(this@MainActivity, "无法读取文件，请重试", Toast.LENGTH_SHORT).show()
                    }
                } else if (isPdf) {
                    Toast.makeText(this@MainActivity, "PDF解析功能暂不支持，请使用AI文本导入方式", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@MainActivity, "不支持的文件格式", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "解析文件失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun saveWithQuizNameDialog(questions: List<Question>, defaultName: String) {
        val editText = android.widget.EditText(this).apply {
            setText(defaultName.removeSuffix(".xlsx").removeSuffix(".xls"))
            hint = "请输入题库名称"
        }
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("题库命名")
            .setMessage("成功解析 ${questions.size} 道题，请为题库命名：")
            .setView(editText)
            .setPositiveButton("保存") { _, _ ->
                val quizName = editText.text.toString().trim().ifEmpty { "默认题库" }
                val questionsWithName = questions.map { it.copy(quizName = quizName) }
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        database.questionDao().insertAll(questionsWithName)
                    }
                    // Flow 自动更新列表，无需手动调用 loadQuestions()
                    Toast.makeText(this@MainActivity, "成功导入${questionsWithName.size}道题到「$quizName」", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun getFileName(uri: Uri): String {
        var name = "默认题库"
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex != -1) {
                name = cursor.getString(nameIndex)
            }
        }
        return name
    }
    
    private fun showTextInputDialog() {
        val scrollView = android.widget.ScrollView(this)
        scrollView.setPadding(48, 16, 48, 16)
        val editText = android.widget.EditText(this).apply {
            hint = "将AI返回的格式化文本粘贴至此..."
            minLines = 5
            maxLines = 12
            gravity = android.view.Gravity.TOP
            layoutParams = android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        scrollView.addView(editText)
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("输入AI处理结果")
            .setMessage("粘贴AI返回的格式化文本，将作为新题库导入")
            .setView(scrollView)
            .setPositiveButton("下一步") { _, _ ->
                val text = editText.text.toString()
                if (text.isNotEmpty()) {
                    parseTextAndSave(text)
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showClipboardConfirmDialog() {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clipData = clipboard.primaryClip
        val clipText = clipData?.getItemAt(0)?.text?.toString()?.trim()

        if (clipText.isNullOrEmpty()) {
            Toast.makeText(this, "剪贴板为空，请先复制AI处理结果", Toast.LENGTH_SHORT).show()
            return
        }

        val previewText = if (clipText.length > 300) clipText.take(300) + "\n\n...（共${clipText.length}字）" else clipText

        android.app.AlertDialog.Builder(this)
            .setTitle("确认剪贴板内容")
            .setMessage("检测到以下内容，是否导入为题库？\n\n$previewText")
            .setPositiveButton("确认导入") { _, _ ->
                parseTextAndSave(clipText)
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun parseTextAndSave(text: String) {
        lifecycleScope.launch {
            try {
                val parsedQuestions = withContext(Dispatchers.IO) {
                    ExcelParser.parseTextContent(text, "临时题库")
                }
                
                if (parsedQuestions.isEmpty()) {
                    Toast.makeText(this@MainActivity, "未解析到有效题目，请检查格式", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                
                // 显示命名对话框
                val nameEditText = android.widget.EditText(this@MainActivity).apply {
                    hint = "例如：数学题库、英语题库"
                }
                
                androidx.appcompat.app.AlertDialog.Builder(this@MainActivity)
                    .setTitle("题库命名")
                    .setMessage("成功解析 ${parsedQuestions.size} 道题，请为题库命名：")
                    .setView(nameEditText)
                    .setPositiveButton("保存") { _, _ ->
                        val quizName = nameEditText.text.toString().trim().ifEmpty { "默认题库" }
                        val questionsWithName = parsedQuestions.map { it.copy(quizName = quizName) }
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                database.questionDao().insertAll(questionsWithName)
                            }
                            // Flow 自动更新列表，无需手动调用 loadQuestions()
                            Toast.makeText(this@MainActivity, "成功导入${questionsWithName.size}道题到「$quizName」", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("取消", null)
                    .show()
                
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "解析文本失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun showEditQuestionDialog(question: Question) {
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }
        
        val typeInput = android.widget.EditText(this).apply {
            setText(question.type)
            hint = "题目类型 (single/multiple/judgment)"
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 16) }
        }
        val contentInput = android.widget.EditText(this).apply {
            setText(question.content)
            hint = "题目内容"
            minLines = 2
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 16) }
        }
        val optionsInput = android.widget.EditText(this).apply {
            setText(question.options.replace("|", " | "))
            hint = "选项（用 | 分隔）"
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 16) }
        }
        val answerInput = android.widget.EditText(this).apply {
            setText(question.correctAnswer)
            hint = "正确答案"
        }
        
        layout.addView(typeInput)
        layout.addView(contentInput)
        layout.addView(optionsInput)
        layout.addView(answerInput)
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("编辑题目")
            .setView(layout)
            .setPositiveButton("保存") { _, _ ->
                val updatedQuestion = question.copy(
                    type = typeInput.text.toString().trim(),
                    content = contentInput.text.toString().trim(),
                    options = optionsInput.text.toString().trim().replace(" ", ""),
                    correctAnswer = answerInput.text.toString().trim()
                )
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        database.questionDao().update(updatedQuestion)
                    }
                    // Flow 自动更新列表，无需手动调用 loadQuestions()
                    Toast.makeText(this@MainActivity, "题目已更新", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun startFloatingWindow() {
        // 检查悬浮窗权限
        if (android.provider.Settings.canDrawOverlays(this)) {
            startService(android.content.Intent(this, com.example.quizhelper.service.FloatingWindowService::class.java))
            Toast.makeText(this, "悬浮窗已启动", Toast.LENGTH_SHORT).show()
        } else {
            // 请求悬浮窗权限
            val intent = Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                data = Uri.parse("package:$packageName")
            }
            startActivity(intent)
            Toast.makeText(this, "请授予悬浮窗权限", Toast.LENGTH_SHORT).show()
        }
    }
}