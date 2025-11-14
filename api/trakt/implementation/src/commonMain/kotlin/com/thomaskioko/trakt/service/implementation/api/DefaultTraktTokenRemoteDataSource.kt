package com.thomaskioko.trakt.service.implementation.api

import com.thomaskioko.trakt.service.implementation.TraktHttpClient
import com.thomaskioko.tvmaniac.core.base.model.Configs
import com.thomaskioko.tvmaniac.trakt.api.TraktTokenRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.AccessTokenBody
import com.thomaskioko.tvmaniac.trakt.api.model.RefreshAccessTokenBody
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAccessRefreshTokenResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAccessTokenResponse
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultTraktTokenRemoteDataSource(
    private val configs: Configs,
    private val httpClient: TraktHttpClient,
) : TraktTokenRemoteDataSource {

    override suspend fun getAccessToken(authCode: String): TraktAccessTokenResponse =
        httpClient
            .post("oauth/token") {
                setBody(
                    AccessTokenBody(
                        code = authCode,
                        clientId = configs.traktClientId,
                        clientSecret = configs.traktClientSecret,
                        redirectUri = configs.traktRedirectUri,
                        grantType = "authorization_code",
                    ),
                )
            }
            .body()

    override suspend fun getAccessRefreshToken(
        refreshToken: String,
    ): TraktAccessRefreshTokenResponse =
        httpClient
            .post("oauth/token") {
                setBody(
                    RefreshAccessTokenBody(
                        refreshToken = refreshToken,
                        clientId = configs.traktClientId,
                        clientSecret = configs.traktClientSecret,
                        redirectUri = configs.traktRedirectUri,
                    ),
                )
            }
            .body()

    override suspend fun revokeAccessToken(authCode: String) {
        httpClient.post("oauth/revoke") {
            setBody(
                AccessTokenBody(
                    code = authCode,
                    clientId = configs.traktClientId,
                    clientSecret = configs.traktClientSecret,
                    redirectUri = configs.traktRedirectUri,
                ),
            )
        }
    }
}
