package com.thomaskioko.tvmanic.trakt.implementation

import com.thomaskioko.tvmaniac.trakt.api.TraktService
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAccessRefreshTokenResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAccessTokenResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddRemoveShowFromListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddShowRequest
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddShowToListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktCreateListRequest
import com.thomaskioko.tvmaniac.trakt.api.model.TraktCreateListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShow
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowIds
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserResponse
import com.thomaskioko.tvmanic.trakt.implementation.model.AccessTokenBody
import com.thomaskioko.tvmanic.trakt.implementation.model.RefreshAccessTokenBody
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
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

    override suspend fun getUserProfile(userId: String): TraktUserResponse =
        httpClient.get("users/$userId") {
            parameter("extended", "full")
        }.body()

    override suspend fun createFavoriteList(userSlug: String): TraktCreateListResponse =
        httpClient.post("users/$userSlug/lists") {
            setBody(TraktCreateListRequest())
        }.body()

    override suspend fun addShowToList(
        userSlug: String,
        listId: Long,
        tmdbShowId: Long
    ): TraktAddShowToListResponse =
        httpClient.post("users/$userSlug/lists/$listId/items") {
            setBody(
                TraktAddShowRequest(
                    shows = listOf(
                        TraktShow(
                            ids = TraktShowIds(
                                tmdbId = tmdbShowId
                            )
                        )
                    )
                )
            )
        }.body()

    override suspend fun deleteShowFromList(
        userSlug: String,
        listId: Long,
        tmdbShowId: Long
    ): TraktAddRemoveShowFromListResponse =
        httpClient.post("users/$userSlug/lists/$listId/items/remove") {
            contentType(ContentType.Application.Json)
            setBody(
                TraktAddShowRequest(
                    shows = listOf(
                        TraktShow(
                            ids = TraktShowIds(
                                tmdbId = tmdbShowId
                            )
                        )
                    )
                )
            )
        }.body()
}