package com.thomaskioko.tvmaniac.core.logger

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class IosCrashReporter internal constructor(
    private val crashlytics: Crashlytics,
    private val configuration: CrashlyticsConfiguration,
) : CrashReporter {

    override fun setCollectionEnabled(enabled: Boolean) {
        if (configuration.isConfigured) configuration.setCollectionEnabled(enabled)
    }

    override fun recordException(throwable: Throwable, keys: Map<String, String>) {
        keys.forEach { (key, value) -> crashlytics.setCustomValue(key, value) }
        crashlytics.sendHandledException(throwable)
        keys.keys.forEach { key -> crashlytics.setCustomValue(key, "") }
    }

    override fun setCustomKey(key: String, value: String) {
        crashlytics.setCustomValue(key, value)
    }

    override fun setUserId(userId: String) {
        crashlytics.setUserId(userId)
    }

    override fun log(message: String) {
        crashlytics.logMessage(message)
    }
}
