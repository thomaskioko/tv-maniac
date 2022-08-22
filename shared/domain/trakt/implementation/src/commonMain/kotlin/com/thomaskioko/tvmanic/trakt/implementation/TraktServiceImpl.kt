package com.thomaskioko.tvmanic.trakt.implementation

import com.thomaskioko.tvmaniac.trakt.api.TraktService
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAccessRefreshTokenResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAccessTokenResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserResponse
import com.thomaskioko.tvmanic.trakt.implementation.model.AccessTokenBody
import com.thomaskioko.tvmanic.trakt.implementation.model.RefreshAccessTokenBody
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class TraktServiceImpl(
    private val clientId: String,
    private val clientSecret: String,
    private val redirectUri: String,
    private val httpClient: HttpClient
) : TraktService {

    override suspend fun getAccessToken(
        authCode: String
    ): TraktAccessTokenResponse = httpClient.post("oauth/token") {
        contentType(ContentType.Application.Json)
        setBody(
            AccessTokenBody(
                code = authCode,
                clientId = clientId,
                clientSecret = clientSecret,
                redirectUri = redirectUri,
                grantType = "authorization_code",
            )
        )
    }.body()


    override suspend fun getAccessRefreshToken(
        refreshToken: String
    ): TraktAccessRefreshTokenResponse = httpClient.post("oauth/token") {
        contentType(ContentType.Application.Json)
        setBody(
            RefreshAccessTokenBody(
                refreshToken = refreshToken
            )
        )
    }.body()

    override suspend fun revokeAccessToken(
        authCode: String
    ) {
        httpClient.post("oauth/revoke") {
            contentType(ContentType.Application.Json)
            setBody(
                AccessTokenBody(
                    code = authCode,
                    clientId = clientId,
                    clientSecret = clientSecret,
                    redirectUri = redirectUri,
                )
            )
        }
    }

    override suspend fun getUserProfile(clientId: String, userId: String): TraktUserResponse =
        httpClient.post("users/$userId") {
            header("Content-Type", ContentType.Application.Json)
            header("trakt-api-version", 2)
            header("trakt-api-key", clientId)
            parameter("extended", "full")
        }.body()
}