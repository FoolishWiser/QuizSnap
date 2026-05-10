package com.example.quizhelper.utils

import com.example.quizhelper.model.Question
import kotlin.math.min

object QuestionMatcher {
    
    /**
     * 在题库中匹配题目
     * @param ocrText OCR识别的文本内容
     * @param questions 题库列表
     * @return 匹配到的题目，如果没有匹配到返回null
     */
    fun matchQuestion(ocrText: String, questions: List<Question>): Question? {
        var bestMatch: Question? = null
        var maxSimilarity = 0.0
        
        for (question in questions) {
            val similarity = calculateSimilarity(ocrText, question.content)
            if (similarity > maxSimilarity && similarity > 0.6) { // 相似度阈值60%
                maxSimilarity = similarity
                bestMatch = question
            }
        }
        
        return bestMatch
    }
    
    /**
     * 计算两个字符串的相似度（基于编辑距离）
     */
    private fun calculateSimilarity(str1: String, str2: String): Double {
        val s1 = normalizeString(str1)
        val s2 = normalizeString(str2)
        
        if (s1.isEmpty() || s2.isEmpty()) return 0.0
        
        val distance = levenshteinDistance(s1, s2)
        val maxLength = maxOf(s1.length, s2.length)
        
        return 1.0 - distance.toDouble() / maxLength
    }
    
    /**
     * 标准化字符串：去除空格、标点符号，转为小写
     */
    private fun normalizeString(str: String): String {
        return str.replace(Regex("[\\s\\p{Punct}]"), "")
            .lowercase()
    }
    
    /**
     * 计算编辑距离
     */
    private fun levenshteinDistance(s1: String, s2: String): Int {
        val m = s1.length
        val n = s2.length
        val dp = Array(m + 1) { IntArray(n + 1) }
        
        for (i in 0..m) dp[i][0] = i
        for (j in 0..n) dp[0][j] = j
        
        for (i in 1..m) {
            for (j in 1..n) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = min(
                    dp[i - 1][j] + 1,      // 删除
                    min(
                        dp[i][j - 1] + 1,  // 插入
                        dp[i - 1][j - 1] + cost  // 替换
                    )
                )
            }
        }
        
        return dp[m][n]
    }
    
    /**
     * 获取题目类型的显示文本
     */
    fun getTypeDisplayText(type: String): String {
        return when (type) {
            "single" -> "单选"
            "multiple" -> "多选"
            "judgment" -> "判断"
            else -> "未知"
        }
    }
    
    /**
     * 获取正确答案的显示文本（文字，非选项）
     */
    fun getAnswerDisplayText(question: Question): String {
        return when (question.type) {
            "judgment" -> {
                when (question.correctAnswer) {
                    "A", "对", "正确", "√" -> "对"
                    "B", "错", "错误", "×" -> "错"
                    else -> question.correctAnswer
                }
            }
            else -> {
                // 对于单选和多选，返回选项内容而不是选项标号
                val options = question.options.split("|")
                val answerIndexes = question.correctAnswer.split(",")
                
                answerIndexes.mapNotNull { index ->
                    val idx = when (index.trim()) {
                        "A" -> 0
                        "B" -> 1
                        "C" -> 2
                        "D" -> 3
                        else -> -1
                    }
                    if (idx in options.indices) options[idx] else null
                }.joinToString(", ")
            }
        }
    }
}
