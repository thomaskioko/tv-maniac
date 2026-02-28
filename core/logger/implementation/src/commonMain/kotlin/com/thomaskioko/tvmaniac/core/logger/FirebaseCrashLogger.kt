package com.thomaskioko.tvmaniac.core.logger

import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, multibinding = true)
public class FirebaseCrashLogger(
    private val crashReporter: CrashReporter,
) : Logger {

    override fun error(message: String, throwable: Throwable) {
        crashReporter.recordException(throwable)
    }

    override fun error(tag: String, message: String) {
        crashReporter.log("[$tag] $message")
    }

    override fun recordException(throwable: Throwable, tag: String) {
        if (tag.isEmpty()) {
            crashReporter.recordException(throwable)
        } else {
            crashReporter.recordException(throwable, tag)
        }
    }

    override fun setUserId(userId: String) {
        crashReporter.setUserId(userId)
    }

    override fun setCustomKey(key: String, value: String) {
        crashReporter.setCustomKey(key, value)
    }
}
