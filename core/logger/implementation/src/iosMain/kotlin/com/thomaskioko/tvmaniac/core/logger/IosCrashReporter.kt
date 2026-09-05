package com.thomaskioko.tvmaniac.core.logger

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class IosCrashReporter(
    private val bridge: CrashReportingBridge,
) : CrashReporter {
    override fun setCollectionEnabled(enabled: Boolean): Unit = bridge.setCollectionEnabled(enabled)

    override fun recordException(throwable: Throwable, keys: Map<String, String>) {
        keys.forEach { (key, value) -> bridge.setCustomKey(key, value) }
        bridge.recordException(throwable, keys[TAG_KEY].orEmpty())
    }

    override fun setCustomKey(key: String, value: String): Unit = bridge.setCustomKey(key, value)
    override fun setUserId(userId: String): Unit = bridge.setUserId(userId)
    override fun log(message: String): Unit = bridge.log(message)
}
