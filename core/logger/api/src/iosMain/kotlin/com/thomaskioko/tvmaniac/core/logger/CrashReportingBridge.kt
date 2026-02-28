package com.thomaskioko.tvmaniac.core.logger

public interface CrashReportingBridge {
    public fun setCollectionEnabled(enabled: Boolean)
    public fun recordException(throwable: Throwable)
    public fun recordException(throwable: Throwable, tag: String)
    public fun setCustomKey(key: String, value: String)
    public fun setUserId(userId: String)
    public fun log(message: String)
}
