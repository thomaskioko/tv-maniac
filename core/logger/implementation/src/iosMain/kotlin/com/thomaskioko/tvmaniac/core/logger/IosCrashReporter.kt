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
    override fun recordException(throwable: Throwable): Unit = bridge.recordException(throwable)
    override fun recordException(throwable: Throwable, tag: String): Unit = bridge.recordException(throwable, tag)
    override fun setCustomKey(key: String, value: String): Unit = bridge.setCustomKey(key, value)
    override fun setUserId(userId: String): Unit = bridge.setUserId(userId)
    override fun log(message: String): Unit = bridge.log(message)
}
