package com.thomaskioko.tvmaniac.traktauth.api

import kotlin.time.Clock
import kotlin.time.Instant

public data class AuthState(
    val accessToken: String,
    val refreshToken: String,
    val isAuthorized: Boolean = true,
    val expiresAt: Instant? = null,
    val tokenLifetimeSeconds: Long? = null,
) {
    public fun isExpiringSoon(): Boolean {
        val expiresAt = this.expiresAt ?: return false
        val lifetime = tokenLifetimeSeconds ?: DEFAULT_LIFETIME_SECONDS
        val thresholdMs = (lifetime * 1000 * REFRESH_THRESHOLD_PERCENT).toLong()
        val now = Clock.System.now()
        return (expiresAt.toEpochMilliseconds() - now.toEpochMilliseconds()) <= thresholdMs
    }

    public companion object {
        public const val DEFAULT_LIFETIME_SECONDS: Long = 86400L
        public const val REFRESH_THRESHOLD_PERCENT: Double = 0.25

        public val Empty: AuthState = AuthState(
            accessToken = "",
            refreshToken = "",
            isAuthorized = false,
        )
    }
}
