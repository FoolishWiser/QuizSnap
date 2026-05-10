package com.example.quizhelper.utils;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J<\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\f2\u0006\u0010\u000e\u001a\u00020\f2\u0014\u0010\u000f\u001a\u0010\u0012\u0006\u0012\u0004\u0018\u00010\u0011\u0012\u0004\u0012\u00020\b0\u0010J\u0010\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u0013\u001a\u00020\u0014H\u0002J\u0016\u0010\u0015\u001a\u00020\u00042\u0006\u0010\u0016\u001a\u00020\u0011H\u0086@\u00a2\u0006\u0002\u0010\u0017R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0018"}, d2 = {"Lcom/example/quizhelper/utils/OCRHelper;", "", "()V", "TAG", "", "recognizer", "Lcom/google/mlkit/vision/text/TextRecognizer;", "captureScreen", "", "mediaProjection", "Landroid/media/projection/MediaProjection;", "width", "", "height", "densityDpi", "callback", "Lkotlin/Function1;", "Landroid/graphics/Bitmap;", "imageToBitmap", "image", "Landroid/media/Image;", "recognizeText", "bitmap", "(Landroid/graphics/Bitmap;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class OCRHelper {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "OCRHelper";
    @org.jetbrains.annotations.NotNull()
    private static final com.google.mlkit.vision.text.TextRecognizer recognizer = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.quizhelper.utils.OCRHelper INSTANCE = null;
    
    private OCRHelper() {
        super();
    }
    
    /**
     * 识别Bitmap中的文字
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object recognizeText(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap bitmap, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    /**
     * 截取屏幕并识别文字
     */
    public final void captureScreen(@org.jetbrains.annotations.NotNull()
    android.media.projection.MediaProjection mediaProjection, int width, int height, int densityDpi, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super android.graphics.Bitmap, kotlin.Unit> callback) {
    }
    
    /**
     * 将Image转换为Bitmap
     */
    private final android.graphics.Bitmap imageToBitmap(android.media.Image image) {
        return null;
    }
}