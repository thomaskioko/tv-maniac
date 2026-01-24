package com.thomaskioko.tvmaniac.core.networkutil.api.extensions

import com.thomaskioko.tvmaniac.core.networkutil.api.ApiRateLimiter
import com.thomaskioko.tvmaniac.core.networkutil.api.model.SyncError
import com.thomaskioko.tvmaniac.core.networkutil.api.model.classifyGenericError

public suspend fun <T> ApiRateLimiter.withRateLimitTracking(
    block: suspend () -> T,
): T {
    return withPermit {
        try {
            val result = block()
            onRequestSuccess()
            result
        } catch (e: Throwable) {
            val syncError = classifyGenericError(e.message)
            if (syncError is SyncError.Retryable.RateLimited) {
                onRateLimited()
            }
            throw e
        }
    }
}
