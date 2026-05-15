package com.example.quizhelper.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizhelper.R
import com.example.quizhelper.data.AppDatabase
import com.example.quizhelper.databinding.ActivityQuizDetailBinding
import com.example.quizhelper.model.Question
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuizDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizDetailBinding
    private lateinit var database: AppDatabase
    private lateinit var questionAdapter: QuestionAdapter
    private val questions = mutableListOf<Question>()
    private var quizName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quizName = intent.getStringExtra("quiz_name") ?: "默认题库"
        database = AppDatabase.getDatabase(this)

        setupUI()
        loadQuestions()
    }

    private fun setupUI() {
        binding.tvQuizDetailTitle.text = quizName

        binding.btnBack.setOnClickListener { finish() }

        questionAdapter = QuestionAdapter { question ->
            showEditQuestionDialog(question)
        }
        binding.recyclerViewQuizQuestions.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewQuizQuestions.adapter = questionAdapter

        // 删除该题库
        binding.btnDeleteQuiz.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("删除题库")
                .setMessage("确定要删除「$quizName」及其所有题目吗？")
                .setPositiveButton("删除") { _, _ ->
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            database.questionDao().deleteByQuizName(quizName)
                        }
                        Toast.makeText(this@QuizDetailActivity, "已删除「$quizName」", Toast.LENGTH_SHORT).show()
                        // 在 UI 线程执行 finish()
                        finish()
                    }
                }
                .setNegativeButton("取消", null)
                .show()
        }
    }

    private fun loadQuestions() {
        lifecycleScope.launch {
            val list = withContext(Dispatchers.IO) {
                database.questionDao().searchByQuizNameList(quizName)
            }
            questions.clear()
            questions.addAll(list)
            questionAdapter.notifyDataSetChanged()
            binding.tvQuizDetailCount.text = "共 ${list.size} 道题"
        }
    }

    private fun showEditQuestionDialog(question: Question) {
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(48, 32, 48, 32)
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
            .setMessage("修改后点击保存")
            .setView(layout)
            .setPositiveButton("保存") { _, _ ->
                val updated = question.copy(
                    type = typeInput.text.toString().trim(),
                    content = contentInput.text.toString().trim(),
                    options = optionsInput.text.toString().trim().replace(" ", ""),
                    correctAnswer = answerInput.text.toString().trim()
                )
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        database.questionDao().update(updated)
                    }
                    loadQuestions()
                    Toast.makeText(this@QuizDetailActivity, "题目已更新", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
}