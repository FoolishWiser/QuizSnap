package com.example.quizhelper.utils;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001e\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u0004J\u001e\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\f\u001a\u00020\u00042\b\b\u0002\u0010\n\u001a\u00020\u0004R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2 = {"Lcom/example/quizhelper/utils/ExcelParser;", "", "()V", "TAG", "", "parseExcel", "", "Lcom/example/quizhelper/model/Question;", "inputStream", "Ljava/io/InputStream;", "quizName", "parseTextContent", "text", "app_debug"})
public final class ExcelParser {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "ExcelParser";
    @org.jetbrains.annotations.NotNull()
    public static final com.example.quizhelper.utils.ExcelParser INSTANCE = null;
    
    private ExcelParser() {
        super();
    }
    
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
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.quizhelper.model.Question> parseExcel(@org.jetbrains.annotations.NotNull()
    java.io.InputStream inputStream, @org.jetbrains.annotations.NotNull()
    java.lang.String quizName) {
        return null;
    }
    
    /**
     * 解析AI处理后的文本（用户从浏览器复制回来的内容）
     * 格式预期为：题目类型|题目内容|选项A|选项B|选项C|选项D|正确答案
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.quizhelper.model.Question> parseTextContent(@org.jetbrains.annotations.NotNull()
    java.lang.String text, @org.jetbrains.annotations.NotNull()
    java.lang.String quizName) {
        return null;
    }
}