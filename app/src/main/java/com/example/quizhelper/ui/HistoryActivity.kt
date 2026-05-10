package com.example.quizhelper.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizhelper.R
import com.example.quizhelper.data.AppDatabase
import com.example.quizhelper.databinding.ActivityHistoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var database: AppDatabase
    private lateinit var quizBankAdapter: QuizBankAdapter
    private val quizBanks = mutableListOf<QuizBankInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(this)

        setupUI()
        loadQuizBanks()
    }

    private fun setupUI() {
        quizBankAdapter = QuizBankAdapter(
            quizBanks,
            onItemClick = { bankInfo ->
                val intent = Intent(this, QuizDetailActivity::class.java).apply {
                    putExtra("quiz_name", bankInfo.name)
                }
                startActivity(intent)
            },
            onDeleteClick = { bankInfo ->
                AlertDialog.Builder(this)
                    .setTitle("删除题库")
                    .setMessage("确定要删除「${bankInfo.name}」及其 ${bankInfo.questionCount} 道题目吗？")
                    .setPositiveButton("删除") { _, _ ->
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                database.questionDao().deleteByQuizName(bankInfo.name)
                            }
                            Toast.makeText(this@HistoryActivity, "已删除「${bankInfo.name}」", Toast.LENGTH_SHORT).show()
                            loadQuizBanks()
                        }
                    }
                    .setNegativeButton("取消", null)
                    .show()
            }
        )
        binding.recyclerViewHistory.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewHistory.adapter = quizBankAdapter

        // 刷新按钮
        binding.btnRefresh.setOnClickListener { loadQuizBanks() }

        // 新增题库按钮
        binding.btnAddQuiz.setOnClickListener {
            finish()
        }
    }

    private fun loadQuizBanks() {
        lifecycleScope.launch {
            val names = withContext(Dispatchers.IO) {
                database.questionDao().getAllQuizNamesList()
            }
            quizBanks.clear()
            for (name in names) {
                val count = withContext(Dispatchers.IO) {
                    database.questionDao().getQuestionCountByQuizName(name)
                }
                val time = withContext(Dispatchers.IO) {
                    database.questionDao().getMinCreateTimeByQuizName(name)
                }
                quizBanks.add(QuizBankInfo(name, count, time))
            }
            quizBankAdapter.notifyDataSetChanged()
            binding.tvCount.text = "共 ${quizBanks.size} 个题库"
        }
    }
}