# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Odds and ends

-keepattributes Signature
-keepattributes *Annotation*

# Apache POI

-dontwarn org.apache.poi.**
-keep class org.apache.poi.** { *; }
-keep class org.openxmlformats.** { *; }

# ML Kit

-keep class com.google.mlkit.** { *; }

# Room

-keep class * extends androidx.room.RoomDatabase
-keep class com.example.quizhelper.model.Question
-keepclassmembers class com.example.quizhelper.model.Question {
    *;
}

# Keep ViewHolder classes

-keep class * extends androidx.recyclerview.widget.RecyclerView$ViewHolder {
    *;
}
