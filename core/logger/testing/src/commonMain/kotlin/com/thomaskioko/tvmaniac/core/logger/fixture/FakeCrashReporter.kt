package com.thomaskioko.tvmaniac.core.logger.fixture

import com.thomaskioko.tvmaniac.core.logger.CrashReporter

public class FakeCrashReporter : CrashReporter {

    override fun setCollectionEnabled(enabled: Boolean) {
    }

    override fun recordException(throwable: Throwable) {}

    override fun recordException(throwable: Throwable, tag: String) {}

    override fun setCustomKey(key: String, value: String) {}

    override fun setUserId(userId: String) {}

    override fun log(message: String) {}
}
