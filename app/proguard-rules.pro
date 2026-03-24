-verbose
-allowaccessmodification
-repackageclasses

# AndroidX + support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**
-dontwarn androidx.**

-dontwarn org.slf4j.impl.StaticLoggerBinder

# For enumeration classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepattributes SourceFile,
                LineNumberTable,
                InnerClasses,
                *Annotation*,
                AnnotationDefault,
                Signature,
                Exceptions

-renamesourcefileattribute SourceFile

# --- Kotlinx Serialization ---
-keepattributes RuntimeVisibleAnnotations
-keep,includedescriptorclasses class com.thomaskioko.tvmaniac.**$$serializer { *; }
-keepclassmembers class com.thomaskioko.tvmaniac.** {
    *** Companion;
}
-keepclasseswithmembers class com.thomaskioko.tvmaniac.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}
-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <2>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}
-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}
-dontwarn kotlinx.serialization.**

# --- Ktor ---
-keepclassmembers class io.ktor.** { volatile <fields>; }
-keep class io.ktor.client.engine.** { *; }
-keep class io.ktor.serialization.** { *; }
-dontwarn io.ktor.**

# --- OkHttp ---
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
-keep class okhttp3.internal.publicsuffix.PublicSuffixDatabase { *; }

# --- Decompose ---
-keep class com.arkivanov.decompose.** { *; }
-keep class com.arkivanov.essenty.** { *; }

# --- kotlin-inject / Metro ---
-keep class me.tatarka.inject.** { *; }
-keep @me.tatarka.inject.annotations.* class * { *; }
-keep class * extends me.tatarka.inject.annotations.Component { *; }
-keepclassmembers class * {
    @me.tatarka.inject.annotations.Inject <init>(...);
    @me.tatarka.inject.annotations.Provides <methods>;
}

# --- SQLDelight ---
-keep class app.cash.sqldelight.** { *; }
-keep class com.thomaskioko.tvmaniac.db.** { *; }

# --- Coil ---
-dontwarn coil3.**
-keep class coil3.** { *; }

# --- Firebase Crashlytics ---
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

# --- AppAuth ---
-keep class net.openid.appauth.** { *; }
-dontwarn net.openid.appauth.**

# --- Coroutines ---
-dontwarn kotlinx.coroutines.**
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# --- General Kotlin ---
-dontwarn kotlin.**
-keep class kotlin.Metadata { *; }
