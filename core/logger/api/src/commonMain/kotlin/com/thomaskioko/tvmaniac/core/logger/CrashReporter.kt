package com.thomaskioko.tvmaniac.core.logger

public interface CrashReporter {

    public fun setCollectionEnabled(enabled: Boolean)

    public fun recordException(throwable: Throwable, keys: Map<String, String> = emptyMap())

    public fun setCustomKey(key: String, value: String)

    public fun setUserId(userId: String)

    public fun log(message: String)
}
