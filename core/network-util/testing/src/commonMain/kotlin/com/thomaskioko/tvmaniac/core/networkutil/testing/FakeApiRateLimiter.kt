package com.thomaskioko.tvmaniac.core.networkutil.testing

import com.thomaskioko.tvmaniac.core.networkutil.api.ApiRateLimiter

public class FakeApiRateLimiter : ApiRateLimiter {

    private var rateLimitedCount: Int = 0
    private var successCount: Int = 0

    override suspend fun <T> withPermit(block: suspend () -> T): T = block()

    override fun onRateLimited() {
        rateLimitedCount++
    }

    override fun onRequestSuccess() {
        successCount++
    }

    public fun getRateLimitedCount(): Int = rateLimitedCount

    public fun getSuccessCount(): Int = successCount

    public fun reset() {
        rateLimitedCount = 0
        successCount = 0
    }
}
