package com.thomaskioko.tvmaniac.util

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

actual class Logger actual constructor(
    private val className: String
) {

    actual fun log(msg: String) {
        if (!BuildConfig().isDebug()) {
            // Crashlytics or whatever
        } else {
            Napier.base(DebugAntilog())

            Napier.d("$className: $msg")
        }
    }
}
