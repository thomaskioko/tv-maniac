package com.thomaskioko.tvmaniac.core.logger

internal class NoOpCrashReportingBridge : CrashReportingBridge {
    override fun setCollectionEnabled(enabled: Boolean) {}
    override fun recordException(throwable: Throwable) {}
    override fun recordException(throwable: Throwable, tag: String) {}
    override fun setCustomKey(key: String, value: String) {}
    override fun setUserId(userId: String) {}
    override fun log(message: String) {}
}
