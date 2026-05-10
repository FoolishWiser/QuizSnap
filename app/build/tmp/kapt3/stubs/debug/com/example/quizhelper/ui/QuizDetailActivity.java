package com.example.quizhelper.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u000e\u001a\u00020\u000fH\u0002J\u0012\u0010\u0010\u001a\u00020\u000f2\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012H\u0014J\b\u0010\u0013\u001a\u00020\u000fH\u0002J\u0010\u0010\u0014\u001a\u00020\u000f2\u0006\u0010\u0015\u001a\u00020\u000bH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lcom/example/quizhelper/ui/QuizDetailActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "binding", "Lcom/example/quizhelper/databinding/ActivityQuizDetailBinding;", "database", "Lcom/example/quizhelper/data/AppDatabase;", "questionAdapter", "Lcom/example/quizhelper/ui/QuestionAdapter;", "questions", "", "Lcom/example/quizhelper/model/Question;", "quizName", "", "loadQuestions", "", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "setupUI", "showEditQuestionDialog", "question", "app_debug"})
public final class QuizDetailActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.example.quizhelper.databinding.ActivityQuizDetailBinding binding;
    private com.example.quizhelper.data.AppDatabase database;
    private com.example.quizhelper.ui.QuestionAdapter questionAdapter;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.example.quizhelper.model.Question> questions = null;
    @org.jetbrains.annotations.NotNull()
    private java.lang.String quizName = "";
    
    public QuizDetailActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void setupUI() {
    }
    
    private final void loadQuestions() {
    }
    
    private final void showEditQuestionDialog(com.example.quizhelper.model.Question question) {
    }
}