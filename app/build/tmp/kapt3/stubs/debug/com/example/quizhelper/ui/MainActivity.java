package com.example.quizhelper.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\b\t\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0010\u001a\u00020\u0011H\u0002J\u0010\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u0013\u001a\u00020\u0014H\u0002J\b\u0010\u0015\u001a\u00020\u0016H\u0002J\u0012\u0010\u0017\u001a\u00020\u00162\b\u0010\u0018\u001a\u0004\u0018\u00010\u0019H\u0014J\u0010\u0010\u001a\u001a\u00020\u00162\u0006\u0010\u001b\u001a\u00020\u0011H\u0002J\u0010\u0010\u001c\u001a\u00020\u00162\u0006\u0010\u0013\u001a\u00020\u0014H\u0002J\u001e\u0010\u001d\u001a\u00020\u00162\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u001e2\u0006\u0010\u001f\u001a\u00020\u0011H\u0002J\b\u0010 \u001a\u00020\u0016H\u0002J\b\u0010!\u001a\u00020\u0016H\u0002J\b\u0010\"\u001a\u00020\u0016H\u0002J\u0010\u0010#\u001a\u00020\u00162\u0006\u0010$\u001a\u00020\u000fH\u0002J\b\u0010%\u001a\u00020\u0016H\u0002J\b\u0010&\u001a\u00020\u0016H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u0007\u001a\u0010\u0012\f\u0012\n \n*\u0004\u0018\u00010\t0\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082.\u00a2\u0006\u0002\n\u0000R\u0014\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\'"}, d2 = {"Lcom/example/quizhelper/ui/MainActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "binding", "Lcom/example/quizhelper/databinding/ActivityMainBinding;", "database", "Lcom/example/quizhelper/data/AppDatabase;", "filePickerLauncher", "Landroidx/activity/result/ActivityResultLauncher;", "Landroid/content/Intent;", "kotlin.jvm.PlatformType", "questionAdapter", "Lcom/example/quizhelper/ui/QuestionAdapter;", "questions", "", "Lcom/example/quizhelper/model/Question;", "getAiPrompt", "", "getFileName", "uri", "Landroid/net/Uri;", "loadQuestions", "", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "parseTextAndSave", "text", "readFileAndParse", "saveWithQuizNameDialog", "", "defaultName", "selectFile", "setupUI", "showClipboardConfirmDialog", "showEditQuestionDialog", "question", "showTextInputDialog", "startFloatingWindow", "app_debug"})
public final class MainActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.example.quizhelper.databinding.ActivityMainBinding binding;
    private com.example.quizhelper.data.AppDatabase database;
    private com.example.quizhelper.ui.QuestionAdapter questionAdapter;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.example.quizhelper.model.Question> questions = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.activity.result.ActivityResultLauncher<android.content.Intent> filePickerLauncher = null;
    
    public MainActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void setupUI() {
    }
    
    private final java.lang.String getAiPrompt() {
        return null;
    }
    
    private final void selectFile() {
    }
    
    private final void readFileAndParse(android.net.Uri uri) {
    }
    
    private final void saveWithQuizNameDialog(java.util.List<com.example.quizhelper.model.Question> questions, java.lang.String defaultName) {
    }
    
    private final java.lang.String getFileName(android.net.Uri uri) {
        return null;
    }
    
    private final void showTextInputDialog() {
    }
    
    private final void showClipboardConfirmDialog() {
    }
    
    private final void parseTextAndSave(java.lang.String text) {
    }
    
    private final void showEditQuestionDialog(com.example.quizhelper.model.Question question) {
    }
    
    private final void loadQuestions() {
    }
    
    private final void startFloatingWindow() {
    }
}