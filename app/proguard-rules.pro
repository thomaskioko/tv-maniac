-verbose
-allowaccessmodification
-repackageclasses

# AndroidX + support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**
-dontwarn androidx.**

-dontwarn org.slf4j.impl.StaticLoggerBinder

# ktor https://github.com/ktorio/ktor/issues/1354
-keepclassmembers class io.ktor.** { volatile <fields>; }

# For enumeration classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepattributes SourceFile,
                LineNumberTable,
                InnerClasses,
                *Annotation*,
                AnnotationDefault

-renamesourcefileattribute SourceFile

-keepattributes *
