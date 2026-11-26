package com.thomaskioko.tvmaniac.core.logger

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class CompositeCrashReporter(
    private val reporters: Set<CrashReporter>,
) : CrashReporter {

    override fun setCollectionEnabled(enabled: Boolean) {
        reporters.forEach { it.setCollectionEnabled(enabled) }
    }

    override fun recordException(throwable: Throwable, keys: Map<String, String>) {
        reporters.forEach { it.recordException(throwable, keys) }
    }

    override fun setCustomKey(key: String, value: String) {
        reporters.forEach { it.setCustomKey(key, value) }
    }

    override fun setUserId(userId: String) {
        reporters.forEach { it.setUserId(userId) }
    }

    override fun log(message: String) {
        reporters.forEach { it.log(message) }
    }
}
