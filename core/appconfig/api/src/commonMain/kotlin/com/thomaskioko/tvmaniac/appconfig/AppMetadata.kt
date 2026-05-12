package com.thomaskioko.tvmaniac.appconfig

public data class AppMetadata(
    val versionName: String,
    val versionCode: Int,
    val packageName: String,
    val platform: Platform,
)

public enum class Platform {
    ANDROID,
    IOS,
}
