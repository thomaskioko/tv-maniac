package com.thomaskioko.tvmaniac.traktauth.api

import kotlin.time.Clock
import kotlin.time.Instant

interface AuthState {
    val accessToken: String
    val refreshToken: String
    val isAuthorized: Boolean
    val expiresAt: Instant?

    fun isExpired(): Boolean {
        val expiresAt = this.expiresAt ?: return false
        return Clock.System.now() >= expiresAt
    }

    fun isExpiringSoon(thresholdMinutes: Int = 60): Boolean {
        val expiresAt = this.expiresAt ?: return false
        val thresholdMs = thresholdMinutes * 60 * 1000
        val now = Clock.System.now()
        return (expiresAt.toEpochMilliseconds() - now.toEpochMilliseconds()) <= thresholdMs
    }

    companion object {
        val Empty: AuthState = SimpleAuthState(
            accessToken = "",
            refreshToken = "",
            isAuthorized = false,
            expiresAt = null,
        )
    }
}

data class SimpleAuthState(
    override val accessToken: String,
    override val refreshToken: String,
    override val isAuthorized: Boolean = true,
    override val expiresAt: Instant? = null,
) : AuthState
