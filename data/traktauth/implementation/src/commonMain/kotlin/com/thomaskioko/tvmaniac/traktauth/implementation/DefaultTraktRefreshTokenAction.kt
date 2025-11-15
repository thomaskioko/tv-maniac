package com.thomaskioko.tvmaniac.traktauth.implementation

import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.trakt.api.TraktTokenRemoteDataSource
import com.thomaskioko.tvmaniac.traktauth.api.AuthState
import com.thomaskioko.tvmaniac.traktauth.api.SimpleAuthState
import com.thomaskioko.tvmaniac.traktauth.api.TraktRefreshTokenAction
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultTraktRefreshTokenAction(
    private val traktTokenDataSource: TraktTokenRemoteDataSource,
    private val logger: Logger,
) : TraktRefreshTokenAction {
    override suspend fun invoke(currentState: AuthState): AuthState? {
        return try {
            logger.debug("TraktRefreshTokenAction: Requesting token refresh")
            val response = traktTokenDataSource.getAccessRefreshToken(
                refreshToken = currentState.refreshToken,
            )

            val accessToken = response.accessToken
            val refreshToken = response.refreshToken
            val expiresIn = response.expiresIn

            if (accessToken == null || refreshToken == null || expiresIn == null) {
                logger.error("TraktRefreshTokenAction", "Invalid response - missing tokens")
                return null
            }

            val expiresAt = Clock.System.now() + expiresIn.seconds

            logger.debug("TraktRefreshTokenAction: Token refresh successful")
            SimpleAuthState(
                accessToken = accessToken,
                refreshToken = refreshToken,
                isAuthorized = true,
                expiresAt = expiresAt,
            )
        } catch (e: Exception) {
            logger.error("TraktRefreshTokenAction: Token refresh failed", e)
            null
        }
    }
}
