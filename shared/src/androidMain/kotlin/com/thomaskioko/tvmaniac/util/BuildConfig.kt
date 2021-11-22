package com.thomaskioko.tvmaniac.util

import com.thomaskioko.tvmaniac.shared.BuildConfig

actual class BuildConfig {
    actual fun isDebug() = BuildConfig.DEBUG
    actual fun isAndroid() = true
}
