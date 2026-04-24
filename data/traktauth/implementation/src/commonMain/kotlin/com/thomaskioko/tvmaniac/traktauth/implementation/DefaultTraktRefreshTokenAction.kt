package com.thomaskioko.tvmaniac.traktauth.implementation

import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.TraktTokenRemoteDataSource
import com.thomaskioko.tvmaniac.traktauth.api.AuthState
import com.thomaskioko.tvmaniac.traktauth.api.RefreshTokenResult
import com.thomaskioko.tvmaniac.traktauth.api.TraktRefreshTokenAction
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTraktRefreshTokenAction(
    private val traktTokenDataSource: TraktTokenRemoteDataSource,
    private val logger: Logger,
) : TraktRefreshTokenAction {
    override suspend fun invoke(currentState: AuthState): RefreshTokenResult {
        logger.debug("TraktRefreshTokenAction", "Requesting token refresh")

        return when (val response = traktTokenDataSource.getAccessRefreshToken(currentState.refreshToken)) {
            is ApiResponse.Success -> {
                val accessToken = response.body.accessToken
                val refreshToken = response.body.refreshToken
                val expiresIn = response.body.expiresIn

                if (accessToken == null || refreshToken == null || expiresIn == null) {
                    logger.error("TraktRefreshTokenAction", "Invalid response - missing tokens")
                    return RefreshTokenResult.Failed("Invalid response - missing tokens")
                }

                val expiresAt = Clock.System.now() + expiresIn.seconds
                logger.debug("TraktRefreshTokenAction", "Token refresh successful, lifetime: ${expiresIn}s")

                RefreshTokenResult.Success(
                    AuthState(
                        accessToken = accessToken,
                        refreshToken = refreshToken,
                        isAuthorized = true,
                        expiresAt = expiresAt,
                        tokenLifetimeSeconds = expiresIn,
                    ),
                )
            }
            is ApiResponse.Error.HttpError -> {
                val isInvalidGrant = response.errorBody?.contains("invalid_grant") == true ||
                    response.errorMessage?.contains("invalid_grant") == true
                val isTokenRevoked = response.code == HTTP_UNAUTHORIZED ||
                    (response.code == HTTP_BAD_REQUEST && isInvalidGrant)

                if (isTokenRevoked) {
                    logger.error("TraktRefreshTokenAction", "Token revoked (${response.code}) - user needs to re-authenticate")
                    RefreshTokenResult.TokenExpired
                } else {
                    logger.error("TraktRefreshTokenAction", "HTTP error: ${response.code}")
                    RefreshTokenResult.Failed("HTTP ${response.code}: ${response.errorMessage}")
                }
            }
            is ApiResponse.Error.SerializationError -> {
                logger.error("TraktRefreshTokenAction", "Serialization error: ${response.message}")
                RefreshTokenResult.Failed("Serialization error: ${response.message}")
            }
            is ApiResponse.Error.NetworkFailure -> {
                logger.error("TraktRefreshTokenAction", "Network failure (${response.kind}): ${response.cause?.message}")
                RefreshTokenResult.NetworkError(response.cause?.message)
            }
            is ApiResponse.Error.OfflineError -> {
                logger.error("TraktRefreshTokenAction", response.errorMessage)
                RefreshTokenResult.NetworkError(response.errorMessage)
            }
            is ApiResponse.Unauthenticated -> {
                logger.error("TraktRefreshTokenAction", "Not authenticated")
                RefreshTokenResult.TokenExpired
            }
        }
    }

    private companion object {
        const val HTTP_BAD_REQUEST = 400
        const val HTTP_UNAUTHORIZED = 401
    }
}
