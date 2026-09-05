package com.thomaskioko.tvmaniac.core.logger

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class IosCrashReporter internal constructor(
    private val crashlytics: Crashlytics,
    private val collection: CrashlyticsCollection,
) : CrashReporter {

    override fun setCollectionEnabled(enabled: Boolean) {
        collection.setEnabled(enabled)
    }

    override fun recordException(throwable: Throwable, keys: Map<String, String>) {
        keys.forEach { (key, value) -> crashlytics.setCustomValue(key, value) }
        crashlytics.sendHandledException(throwable)
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
