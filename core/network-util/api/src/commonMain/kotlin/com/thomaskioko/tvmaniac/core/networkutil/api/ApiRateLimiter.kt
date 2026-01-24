package com.thomaskioko.tvmaniac.core.networkutil.api

public interface ApiRateLimiter {
    public suspend fun <T> withPermit(block: suspend () -> T): T

    public fun onRateLimited() {}

    public fun onRequestSuccess() {}
}
