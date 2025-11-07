# Room Database
-keep class androidx.room.RoomDatabase
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keepclassmembers class * {
    @androidx.room.* *;
}

# Koin DI
-keep class org.koin.** { *; }
-keepclassmembers class * {
    @org.koin.core.annotation.Single <init>(...);
    @org.koin.core.annotation.Factory <init>(...);
}

# ML Kit
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.** { *; }

# CameraX
-keep class androidx.camera.** { *; }

# Media3
-keep class androidx.media3.** { *; }

# Compose
-keep class androidx.compose.** { *; }

# Coroutines
-keep class kotlinx.coroutines.** { *; }

# Serialization
-keep @kotlinx.serialization.Serializable class *

# Data classes
-keepclassmembers class * {
    public <init>(...);
}

# WorkManager
-keep class androidx.work.** { *; }

# Keep our application class
-keep public class com.aktarjabed.nextgenrecorder.NextGenRecorderApp

# Keep ViewModels
-keep class * extends androidx.lifecycle.ViewModel {
    public <init>(...);
}

# Keep the models
-keep class com.aktarjabed.nextgenrecorder.domain.model.** { *; }

# Crashlytics (if added later)
-keep class com.google.firebase.crashlytics.** { *; }
-dontwarn com.google.firebase.crashlytics.**

# Optimizations
-optimizations !code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
