package com.example.quizhelper.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class Question(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // 题目类型：single(单选), multiple(多选), judgment(判断)
    val type: String,
    
    // 题目内容
    val content: String,
    
    // 选项列表，用特殊字符分隔
    val options: String,
    
    // 正确答案，对于单选是单个选项，多选是多个选项用分隔符分开，判断是对/错
    val correctAnswer: String,
    
    // 创建时间
    val createTime: Long = System.currentTimeMillis(),
    
    // 所属题库名称
    val quizName: String = "默认题库"
)
