package com.thomaskioko.tvmaniac.appconfig

public data class ApplicationInfo(
    val versionName: String,
    val versionCode: Int,
    val packageName: String,
    val debugBuild: Boolean,
    val platform: Platform,
)

public enum class Platform {
    ANDROID,
    IOS,
}
