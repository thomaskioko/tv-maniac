-verbose
-allowaccessmodification
-repackageclasses

# AndroidX + support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**

-dontwarn org.slf4j.impl.StaticLoggerBinder

-keepattributes SourceFile,
                LineNumberTable,
                InnerClasses,
                EnclosingMethod,
                *Annotation*,
                AnnotationDefault,
                Signature,
                Exceptions

-renamesourcefileattribute SourceFile

# --- Metro DI ---
# Keep Metro annotations and annotated members
-keep @dev.zacsweers.metro.* class *
-keepclassmembers class * {
    @dev.zacsweers.metro.Inject <init>(...);
    @dev.zacsweers.metro.Provides <methods>;
    @dev.zacsweers.metro.AssistedInject <init>(...);
}

# Keep Metro generated code (Factories, Contributions, and Implementations)
-keep class *.*Metro* { *; }
-keep class *Impl { *; }
-keep class *FactoryImpl { *; }
-keep class *BindingContainer { *; }

# --- Firebase Crashlytics ---
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

# --- Strip Android Logs from production ---
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
}

# --- Strip Jetpack Compose tracing from production ---
-assumenosideeffects class androidx.compose.runtime.ComposerKt {
    boolean isTraceInProgress();
    void traceEventStart(int, int, int, java.lang.String);
    void traceEventEnd();
}

# --- General Kotlin ---
-dontwarn kotlin.**
-keep class kotlin.Metadata { *; }
