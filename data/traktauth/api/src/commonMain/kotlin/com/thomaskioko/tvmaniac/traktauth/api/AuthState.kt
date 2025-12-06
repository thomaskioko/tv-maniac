package com.thomaskioko.tvmaniac.traktauth.api

import kotlin.time.Clock
import kotlin.time.Instant

data class AuthState(
    val accessToken: String,
    val refreshToken: String,
    val isAuthorized: Boolean = true,
    val expiresAt: Instant? = null,
    val tokenLifetimeSeconds: Long? = null,
) {
    fun isExpiringSoon(): Boolean {
        val expiresAt = this.expiresAt ?: return false
        val lifetime = tokenLifetimeSeconds ?: DEFAULT_LIFETIME_SECONDS
        val thresholdMs = (lifetime * 1000 * REFRESH_THRESHOLD_PERCENT).toLong()
        val now = Clock.System.now()
        return (expiresAt.toEpochMilliseconds() - now.toEpochMilliseconds()) <= thresholdMs
    }

    companion object {
        const val DEFAULT_LIFETIME_SECONDS = 86400L
        const val REFRESH_THRESHOLD_PERCENT = 0.25

        val Empty = AuthState(
            accessToken = "",
            refreshToken = "",
            isAuthorized = false,
        )
    }
}
