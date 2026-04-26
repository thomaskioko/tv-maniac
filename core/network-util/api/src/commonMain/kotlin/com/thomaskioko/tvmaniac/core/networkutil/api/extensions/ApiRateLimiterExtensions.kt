package com.thomaskioko.tvmaniac.core.networkutil.api.extensions

import com.thomaskioko.tvmaniac.core.networkutil.api.ApiRateLimiter
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiHttpException
import com.thomaskioko.tvmaniac.core.networkutil.api.model.SyncError
import com.thomaskioko.tvmaniac.core.networkutil.api.model.SyncException

public suspend fun <T> ApiRateLimiter.withRateLimitTracking(
    block: suspend () -> T,
): T {
    return withPermit {
        try {
            val result = block()
            onRequestSuccess()
            result
        } catch (e: Throwable) {
            if (e.isRateLimited()) onRateLimited()
            throw e
        }
    }
}

private fun Throwable.isRateLimited(): Boolean = when (this) {
    is SyncException -> syncError is SyncError.Retryable.RateLimited
    is ApiHttpException -> code == HTTP_TOO_MANY_REQUESTS
    else -> (cause as? SyncException)?.syncError is SyncError.Retryable.RateLimited
}

private const val HTTP_TOO_MANY_REQUESTS = 429
