package com.thomaskioko.tvmaniac.core.logger.fixture

import com.thomaskioko.tvmaniac.core.logger.CrashReporter
import com.thomaskioko.tvmaniac.core.logger.FirebaseCrashLogger
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, replaces = [FirebaseCrashLogger::class])
public class FakeCrashReporter : CrashReporter {
    override fun setCollectionEnabled(enabled: Boolean) {}
    override fun recordException(throwable: Throwable) {}
    override fun recordException(throwable: Throwable, tag: String) {}
    override fun setCustomKey(key: String, value: String) {}
    override fun setUserId(userId: String) {}
    override fun log(message: String) {}
}
