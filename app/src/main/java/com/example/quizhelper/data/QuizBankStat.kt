package com.example.quizhelper.data

import androidx.room.ColumnInfo

data class QuizBankStat(
    @ColumnInfo(name = "quizName")
    val quizName: String,
    @ColumnInfo(name = "questionCount")
    val questionCount: Int,
    @ColumnInfo(name = "minCreateTime")
    val minCreateTime: Long
)