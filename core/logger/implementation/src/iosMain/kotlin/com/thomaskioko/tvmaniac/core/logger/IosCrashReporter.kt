package com.thomaskioko.tvmaniac.core.logger

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock

@SingleIn(AppScope::class)
@ContributesIntoSet(AppScope::class)
public class IosCrashReporter internal constructor(
    private val crashlytics: Crashlytics,
    private val configuration: CrashlyticsConfiguration,
) : CrashReporter {

    private val lock = reentrantLock()
    private val customKeys: MutableMap<String, String> = mutableMapOf()

    override fun setCollectionEnabled(enabled: Boolean) {
        if (configuration.isConfigured) configuration.setCollectionEnabled(enabled)
    }

    override fun recordException(throwable: Throwable, keys: Map<String, String>): Unit = lock.withLock {
        keys.forEach { (key, value) -> crashlytics.setCustomValue(key, value) }
        crashlytics.sendHandledException(throwable)
        keys.keys.forEach { key -> crashlytics.setCustomValue(key, customKeys[key].orEmpty()) }
    }

    override fun setCustomKey(key: String, value: String): Unit = lock.withLock {
        customKeys[key] = value
        crashlytics.setCustomValue(key, value)
    }

    override fun setUserId(userId: String) {
        crashlytics.setUserId(userId)
    }

    override fun log(message: String) {
        crashlytics.logMessage(message)
    }
}
