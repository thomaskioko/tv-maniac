package com.thomaskioko.trakt.service.implementation.api

import com.thomaskioko.tvmaniac.core.base.TraktApi
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.safeRequest
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.TraktTokenRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.AccessTokenBody
import com.thomaskioko.tvmaniac.trakt.api.model.RefreshAccessTokenBody
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAccessRefreshTokenResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAccessTokenResponse
import com.thomaskioko.tvmaniac.util.api.BuildConfig
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
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
public class DefaultTraktTokenRemoteDataSource(
    @TraktApi
    private val httpClient: HttpClient,
) : TraktTokenRemoteDataSource {

    private val traktClientId: String = BuildConfig.TRAKT_CLIENT_ID
    private val traktClientSecret: String = BuildConfig.TRAKT_CLIENT_SECRET

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
                    clientId = traktClientId,
                    clientSecret = traktClientSecret,
                    redirectUri = BuildConfig.TRAKT_REDIRECT_URI,
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
                    clientId = traktClientId,
                    clientSecret = traktClientSecret,
                    redirectUri = BuildConfig.TRAKT_REDIRECT_URI,
                ),
            )
        }

    override suspend fun revokeAccessToken(authCode: String) {
        httpClient.post("oauth/revoke") {
            setBody(
                AccessTokenBody(
                    code = authCode,
                    clientId = traktClientId,
                    clientSecret = traktClientSecret,
                    redirectUri = BuildConfig.TRAKT_REDIRECT_URI,
                ),
            )
        }
    }
}
