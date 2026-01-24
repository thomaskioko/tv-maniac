package com.thomaskioko.tvmaniac.core.networkutil.ratelimit

import com.thomaskioko.tvmaniac.core.networkutil.api.ApiRateLimiter
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.random.Random

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class AdaptiveApiRateLimiter : ApiRateLimiter {

    private val semaphore = Semaphore(MAX_CONCURRENT_API_CALLS)

    private val _backoffMultiplier = atomic(0)
    private val _consecutiveSuccesses = atomic(0)

    override suspend fun <T> withPermit(block: suspend () -> T): T {
        val currentBackoff = _backoffMultiplier.value
        if (currentBackoff > 0) {
            val exponentialDelay = calculateExponentialDelay(currentBackoff)
            val jitter = Random.nextLong(0, MAX_JITTER_MS)
            delay(exponentialDelay + jitter)
        }

        return semaphore.withPermit { block() }
    }

    override fun onRateLimited() {
        _consecutiveSuccesses.value = 0
        val current = _backoffMultiplier.value
        if (current < MAX_BACKOFF_MULTIPLIER) {
            _backoffMultiplier.compareAndSet(current, current + 1)
        }
    }

    override fun onRequestSuccess() {
        val currentBackoff = _backoffMultiplier.value
        if (currentBackoff > 0) {
            val newSuccessCount = _consecutiveSuccesses.incrementAndGet()
            if (newSuccessCount >= SUCCESSES_BEFORE_RECOVERY) {
                _backoffMultiplier.compareAndSet(currentBackoff, currentBackoff - 1)
                _consecutiveSuccesses.value = 0
            }
        }
    }

    internal fun getBackoffMultiplier(): Int = _backoffMultiplier.value

    internal fun getConsecutiveSuccesses(): Int = _consecutiveSuccesses.value

    private fun calculateExponentialDelay(multiplier: Int): Long {
        val exponentialDelay = BACKOFF_BASE_MS * (1 shl (multiplier - 1))
        return minOf(exponentialDelay, MAX_BACKOFF_MS)
    }

    public companion object {
        internal const val MAX_CONCURRENT_API_CALLS: Int = 6
        internal const val BACKOFF_BASE_MS: Long = 1000L
        internal const val MAX_BACKOFF_MS: Long = 32_000L
        internal const val MAX_BACKOFF_MULTIPLIER: Int = 6
        internal const val MAX_JITTER_MS: Long = 1000L
        internal const val SUCCESSES_BEFORE_RECOVERY: Int = 5
    }
}
