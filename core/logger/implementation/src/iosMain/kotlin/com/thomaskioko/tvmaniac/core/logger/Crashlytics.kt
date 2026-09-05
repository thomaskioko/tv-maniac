package com.thomaskioko.tvmaniac.core.logger

public interface Crashlytics {
    public fun setCustomValue(key: String, value: String)
    public fun sendHandledException(throwable: Throwable)
    public fun setUserId(userId: String)
    public fun logMessage(message: String)
}
