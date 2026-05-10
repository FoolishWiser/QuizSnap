package com.example.quizhelper.data

import androidx.room.*
import com.example.quizhelper.model.Question
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(question: Question): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<Question>)
    
    @Update
    suspend fun update(question: Question)
    
    @Delete
    suspend fun delete(question: Question)
    
    @Query("SELECT * FROM questions ORDER BY createTime DESC")
    fun getAllQuestions(): Flow<List<Question>>
    
    @Query("SELECT * FROM questions WHERE quizName = :quizName ORDER BY createTime DESC")
    fun getQuestionsByQuizName(quizName: String): Flow<List<Question>>
    
    @Query("SELECT DISTINCT quizName FROM questions ORDER BY createTime DESC")
    fun getAllQuizNames(): Flow<List<String>>
    
    @Query("DELETE FROM questions WHERE quizName = :quizName")
    suspend fun deleteByQuizName(quizName: String)
    
    @Query("SELECT * FROM questions")
    suspend fun getAllQuestionsList(): List<Question>
    
    @Query("SELECT * FROM questions WHERE content LIKE '%' || :keyword || '%'")
    suspend fun searchByContent(keyword: String): List<Question>

    @Query("SELECT * FROM questions WHERE quizName = :quizName")
    suspend fun searchByQuizNameList(quizName: String): List<Question>

    @Query("SELECT DISTINCT quizName FROM questions")
    suspend fun getAllQuizNamesList(): List<String>

    @Query("SELECT MIN(createTime) FROM questions WHERE quizName = :quizName")
    suspend fun getMinCreateTimeByQuizName(quizName: String): Long

    @Query("SELECT COUNT(*) FROM questions WHERE quizName = :quizName")
    suspend fun getQuestionCountByQuizName(quizName: String): Int
}
