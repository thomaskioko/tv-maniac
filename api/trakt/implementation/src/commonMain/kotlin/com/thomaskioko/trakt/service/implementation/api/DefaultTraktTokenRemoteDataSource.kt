package com.thomaskioko.trakt.service.implementation.api

import com.thomaskioko.tvmaniac.core.base.TraktApi
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.safeRequest
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.TraktConfig
import com.thomaskioko.tvmaniac.trakt.api.TraktTokenRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.AccessTokenBody
import com.thomaskioko.tvmaniac.trakt.api.model.RefreshAccessTokenBody
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAccessRefreshTokenResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAccessTokenResponse
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.AuthCircuitBreaker
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.path

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
public class DefaultTraktTokenRemoteDataSource(
    @TraktApi
    private val httpClient: HttpClient,
    private val traktConfig: TraktConfig,
) : TraktTokenRemoteDataSource {

    override suspend fun getAccessToken(authCode: String): ApiResponse<TraktAccessTokenResponse> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Post
                path("oauth/token")
            }
            contentType(ContentType.Application.Json)
            setBody(
                AccessTokenBody(
                    code = authCode,
                    clientId = traktConfig.clientId,
                    clientSecret = traktConfig.clientSecret,
                    redirectUri = traktConfig.redirectUri,
                    grantType = "authorization_code",
                ),
            )
        }

    override suspend fun getAccessRefreshToken(
        refreshToken: String,
    ): ApiResponse<TraktAccessRefreshTokenResponse> =
        httpClient.safeRequest {
            attributes.put(AuthCircuitBreaker, Unit)
            url {
                method = HttpMethod.Post
                path("oauth/token")
            }
            contentType(ContentType.Application.Json)
            setBody(
                RefreshAccessTokenBody(
                    refreshToken = refreshToken,
                    clientId = traktConfig.clientId,
                    clientSecret = traktConfig.clientSecret,
                    redirectUri = traktConfig.redirectUri,
                ),
            )
        }

    override suspend fun revokeAccessToken(authCode: String) {
        httpClient.post("oauth/revoke") {
            setBody(
                AccessTokenBody(
                    code = authCode,
                    clientId = traktConfig.clientId,
                    clientSecret = traktConfig.clientSecret,
                    redirectUri = traktConfig.redirectUri,
                ),
            )
        }
    }
}
