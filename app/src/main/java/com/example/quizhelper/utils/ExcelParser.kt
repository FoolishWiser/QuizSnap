package com.example.quizhelper.utils

import android.util.Log
import com.example.quizhelper.model.Question
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream

object ExcelParser {
    
    private const val TAG = "ExcelParser"
    
    /**
     * 解析Excel文件，提取题目信息
     * 期望的Excel格式：
     * 列1: 题目类型 (单选/多选/判断)
     * 列2: 题目内容
     * 列3: 选项A
     * 列4: 选项B
     * 列5: 选项C
     * 列6: 选项D
     * 列7: 正确答案 (A/B/C/D 或 对/错)
     */
    fun parseExcel(inputStream: InputStream, quizName: String = "默认题库"): List<Question> {
        val questions = mutableListOf<Question>()

        try {
            // 使用 use 块自动关闭 Workbook
            WorkbookFactory.create(inputStream).use { workbook ->
                val sheet = workbook.getSheetAt(0)

                // 空表格检查
                if (sheet.lastRowNum < 1) {
                    return emptyList()
                }

                // 跳过表头，从第二行开始
                for (rowIndex in 1..sheet.lastRowNum) {
                    val row = sheet.getRow(rowIndex) ?: continue

                    try {
                        // 读取题目类型
                        val typeCell = row.getCell(0)
                        val type = when (typeCell?.stringCellValue?.trim()) {
                            "单选" -> "single"
                            "多选" -> "multiple"
                            "判断" -> "judgment"
                            else -> continue
                        }

                        // 读取题目内容
                        val contentCell = row.getCell(1)
                        val content = contentCell?.stringCellValue?.trim() ?: continue

                        // 读取选项
                        val options = mutableListOf<String>()
                        for (i in 2..5) {
                            val optionCell = row.getCell(i)
                            val option = optionCell?.stringCellValue?.trim()
                            if (!option.isNullOrEmpty()) {
                                options.add(option)
                            }
                        }

                        // 读取正确答案
                        val answerCell = row.getCell(6)
                        val correctAnswer = answerCell?.stringCellValue?.trim() ?: ""

                        // 创建Question对象
                        val question = Question(
                            type = type,
                            content = content,
                            options = options.joinToString("|"),
                            correctAnswer = correctAnswer,
                            quizName = quizName
                        )

                        questions.add(question)

                    } catch (e: Exception) {
                        Log.e(TAG, "解析第${rowIndex + 1}行时出错: ${e.message}")
                    }
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "解析Excel文件时出错: ${e.message}")
            throw e
        }

        return questions
    }
    
    /**
     * 解析AI处理后的文本（用户从浏览器复制回来的内容）
     * 格式预期为：题目类型|题目内容|选项A|选项B|选项C|选项D|正确答案
     */
    fun parseTextContent(text: String, quizName: String = "默认题库"): List<Question> {
        val questions = mutableListOf<Question>()
        val lines = text.split("\n")
        
        for (line in lines) {
            if (line.trim().isEmpty()) continue
            
            try {
                val parts = line.split("|")
                if (parts.size < 3) continue
                
                val type = when (parts[0].trim()) {
                    "单选" -> "single"
                    "多选" -> "multiple"
                    "判断" -> "judgment"
                    else -> continue
                }
                
                val content = parts[1].trim()
                val options = if (parts.size > 3) {
                    parts.subList(2, parts.size - 1).joinToString("|") { it.trim() }
                } else ""
                
                val correctAnswer = parts.last().trim()
                
                val question = Question(
                    type = type,
                    content = content,
                    options = options,
                    correctAnswer = correctAnswer,
                    quizName = quizName
                )
                
                questions.add(question)
                
            } catch (e: Exception) {
                Log.e(TAG, "解析文本行时出错: ${e.message}")
            }
        }
        
        return questions
    }
}
