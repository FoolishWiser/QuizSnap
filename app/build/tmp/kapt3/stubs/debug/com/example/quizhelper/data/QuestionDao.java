package com.example.quizhelper.data;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0005\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0002\b\n\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0007\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\nJ\u0014\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\r0\fH\'J\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00050\rH\u00a7@\u00a2\u0006\u0002\u0010\u000fJ\u0014\u0010\u0010\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\r0\fH\'J\u0014\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\t0\rH\u00a7@\u00a2\u0006\u0002\u0010\u000fJ\u0016\u0010\u0012\u001a\u00020\u00132\u0006\u0010\b\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\nJ\u0016\u0010\u0014\u001a\u00020\u00152\u0006\u0010\b\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\nJ\u001c\u0010\u0016\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\r0\f2\u0006\u0010\b\u001a\u00020\tH\'J\u0016\u0010\u0017\u001a\u00020\u00132\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u001c\u0010\u0018\u001a\u00020\u00032\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00050\rH\u00a7@\u00a2\u0006\u0002\u0010\u001aJ\u001c\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00050\r2\u0006\u0010\u001c\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\nJ\u001c\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00050\r2\u0006\u0010\b\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\nJ\u0016\u0010\u001e\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006\u00a8\u0006\u001f"}, d2 = {"Lcom/example/quizhelper/data/QuestionDao;", "", "delete", "", "question", "Lcom/example/quizhelper/model/Question;", "(Lcom/example/quizhelper/model/Question;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteByQuizName", "quizName", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllQuestions", "Lkotlinx/coroutines/flow/Flow;", "", "getAllQuestionsList", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllQuizNames", "getAllQuizNamesList", "getMinCreateTimeByQuizName", "", "getQuestionCountByQuizName", "", "getQuestionsByQuizName", "insert", "insertAll", "questions", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "searchByContent", "keyword", "searchByQuizNameList", "update", "app_debug"})
@androidx.room.Dao()
public abstract interface QuestionDao {
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.example.quizhelper.model.Question question, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertAll(@org.jetbrains.annotations.NotNull()
    java.util.List<com.example.quizhelper.model.Question> questions, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object update(@org.jetbrains.annotations.NotNull()
    com.example.quizhelper.model.Question question, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    com.example.quizhelper.model.Question question, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM questions ORDER BY createTime DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.quizhelper.model.Question>> getAllQuestions();
    
    @androidx.room.Query(value = "SELECT * FROM questions WHERE quizName = :quizName ORDER BY createTime DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.quizhelper.model.Question>> getQuestionsByQuizName(@org.jetbrains.annotations.NotNull()
    java.lang.String quizName);
    
    @androidx.room.Query(value = "SELECT DISTINCT quizName FROM questions ORDER BY createTime DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<java.lang.String>> getAllQuizNames();
    
    @androidx.room.Query(value = "DELETE FROM questions WHERE quizName = :quizName")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteByQuizName(@org.jetbrains.annotations.NotNull()
    java.lang.String quizName, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM questions")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAllQuestionsList(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.quizhelper.model.Question>> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM questions WHERE content LIKE \'%\' || :keyword || \'%\'")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object searchByContent(@org.jetbrains.annotations.NotNull()
    java.lang.String keyword, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.quizhelper.model.Question>> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM questions WHERE quizName = :quizName")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object searchByQuizNameList(@org.jetbrains.annotations.NotNull()
    java.lang.String quizName, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.quizhelper.model.Question>> $completion);
    
    @androidx.room.Query(value = "SELECT DISTINCT quizName FROM questions")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAllQuizNamesList(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<java.lang.String>> $completion);
    
    @androidx.room.Query(value = "SELECT MIN(createTime) FROM questions WHERE quizName = :quizName")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getMinCreateTimeByQuizName(@org.jetbrains.annotations.NotNull()
    java.lang.String quizName, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM questions WHERE quizName = :quizName")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getQuestionCountByQuizName(@org.jetbrains.annotations.NotNull()
    java.lang.String quizName, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
}