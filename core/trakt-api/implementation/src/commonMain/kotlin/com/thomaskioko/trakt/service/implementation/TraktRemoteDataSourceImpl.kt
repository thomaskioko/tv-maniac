package com.thomaskioko.trakt.service.implementation

import com.thomaskioko.tvmaniac.core.networkutil.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.safeRequest
import com.thomaskioko.tvmaniac.trakt.api.TraktRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.AccessTokenBody
import com.thomaskioko.tvmaniac.trakt.api.model.ErrorResponse
import com.thomaskioko.tvmaniac.trakt.api.model.RefreshAccessTokenBody
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAccessRefreshTokenResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAccessTokenResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddRemoveShowFromListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddShowRequest
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddShowToListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktCreateListRequest
import com.thomaskioko.tvmaniac.trakt.api.model.TraktCreateListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktFollowedShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktPersonalListsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonEpisodesResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShow
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowIds
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserStatsResponse
import com.thomaskioko.tvmaniac.util.ExceptionHandler
import com.thomaskioko.tvmaniac.util.model.Configs
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.path
import me.tatarka.inject.annotations.Inject

private const val PAGE_LIMIT_SIZE = 20

@Inject
class TraktRemoteDataSourceImpl(
    private val configs: Configs,
    private val httpClient: TraktHttpClient,
    private val exceptionHandler: ExceptionHandler,
) : TraktRemoteDataSource {

    override suspend fun getAccessToken(
        authCode: String,
    ): TraktAccessTokenResponse = httpClient.post("oauth/token") {
        setBody(
            AccessTokenBody(
                code = authCode,
                clientId = configs.traktClientId,
                clientSecret = configs.traktClientSecret,
                redirectUri = configs.traktRedirectUri,
                grantType = "authorization_code",
            ),
        )
    }.body()

    override suspend fun getAccessRefreshToken(
        refreshToken: String,
    ): TraktAccessRefreshTokenResponse = httpClient.post("oauth/token") {
        setBody(
            RefreshAccessTokenBody(
                refreshToken = refreshToken,
            ),
        )
    }.body()

    override suspend fun revokeAccessToken(
        authCode: String,
    ) {
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

    override suspend fun getUser(userId: String): ApiResponse<TraktUserResponse, ErrorResponse> =
        httpClient.safeRequest(exceptionHandler) {
            url {
                method = HttpMethod.Get
                path("users/$userId")
                parameter("extended", "full")
            }
        }

    override suspend fun getUserList(userId: String): List<TraktPersonalListsResponse> =
        httpClient.get("users/$userId/lists").body()

    override suspend fun getUserStats(userId: String): TraktUserStatsResponse =
        httpClient.get("users/$userId/stats").body()

    override suspend fun createFollowingList(userSlug: String): TraktCreateListResponse =
        httpClient.post("users/$userSlug/lists") {
            setBody(TraktCreateListRequest())
        }.body()

    override suspend fun getFollowedList(
        listId: Long,
        userSlug: String,
    ): List<TraktFollowedShowResponse> =
        httpClient.get("users/$userSlug/lists/$listId/items/shows") {
            parameter("sort_by", "added")
        }.body()

    override suspend fun getWatchList(): List<TraktFollowedShowResponse> =
        httpClient.get("sync/watchlist/shows") {
            parameter("sort_by", "added")
        }.body()

    override suspend fun addShowToWatchList(showId: Long): TraktAddShowToListResponse =
        httpClient.post("sync/watchlist") {
            setBody(
                TraktAddShowRequest(
                    shows = listOf(
                        TraktShow(
                            ids = TraktShowIds(
                                traktId = showId.toInt(),
                            ),
                        ),
                    ),
                ),
            )
        }.body()

    override suspend fun removeShowFromWatchList(showId: Long): TraktAddRemoveShowFromListResponse =
        httpClient.post("sync/watchlist/remove") {
            contentType(ContentType.Application.Json)
            setBody(
                TraktAddShowRequest(
                    shows = listOf(
                        TraktShow(
                            ids = TraktShowIds(
                                traktId = showId.toInt(),
                            ),
                        ),
                    ),
                ),
            )
        }.body()

    override suspend fun addShowToList(
        userSlug: String,
        listId: Long,
        traktShowId: Long,
    ): TraktAddShowToListResponse =
        httpClient.post("users/$userSlug/lists/$listId/items") {
            setBody(
                TraktAddShowRequest(
                    shows = listOf(
                        TraktShow(
                            ids = TraktShowIds(
                                traktId = traktShowId.toInt(),
                            ),
                        ),
                    ),
                ),
            )
        }.body()

    override suspend fun deleteShowFromList(
        userSlug: String,
        listId: Long,
        traktShowId: Long,
    ): TraktAddRemoveShowFromListResponse =
        httpClient.post("users/$userSlug/lists/$listId/items/remove") {
            contentType(ContentType.Application.Json)
            setBody(
                TraktAddShowRequest(
                    shows = listOf(
                        TraktShow(
                            ids = TraktShowIds(traktId = traktShowId.toInt()),
                        ),
                    ),
                ),
            )
        }.body()

    override suspend fun getTrendingShows(page: Long): ApiResponse<List<TraktShowsResponse>, ErrorResponse> =
        httpClient.safeRequest(exceptionHandler) {
            url {
                method = HttpMethod.Get
                path("shows/trending")
                parameter("extended", "full")
                parameter("limit", PAGE_LIMIT_SIZE)
                parameter("page", "$page")
            }
        }

    override suspend fun getRecommendedShows(
        page: Long,
        period: String,
    ): ApiResponse<List<TraktShowsResponse>, ErrorResponse> =
        httpClient.safeRequest(exceptionHandler) {
            url {
                method = HttpMethod.Get
                path("shows/recommended/$period")
                parameter("extended", "full")
                parameter("limit", PAGE_LIMIT_SIZE)
                parameter("page", "$page")
            }
        }

    override suspend fun getAnticipatedShows(page: Long): ApiResponse<List<TraktShowsResponse>, ErrorResponse> =
        httpClient.safeRequest(exceptionHandler) {
            url {
                method = HttpMethod.Get
                path("shows/anticipated")
                parameter("extended", "full")
                parameter("limit", PAGE_LIMIT_SIZE)
                parameter("page", "$page")
            }
        }

    override suspend fun getPopularShows(page: Long): ApiResponse<List<TraktShowResponse>, ErrorResponse> =
        httpClient.safeRequest(exceptionHandler) {
            url {
                method = HttpMethod.Get
                path("shows/popular")
                parameter("extended", "full")
                parameter("limit", PAGE_LIMIT_SIZE)
                parameter("page", "$page")
            }
        }

    override suspend fun getSimilarShows(traktId: Long): ApiResponse<List<TraktShowResponse>, ErrorResponse> =
        httpClient.safeRequest(exceptionHandler) {
            url {
                method = HttpMethod.Get
                path("shows/$traktId/related")
                parameter("extended", "full")
            }
        }

    override suspend fun getShowSeasons(traktId: Long): ApiResponse<List<TraktSeasonsResponse>, ErrorResponse> =
        httpClient.safeRequest(exceptionHandler) {
            url {
                method = HttpMethod.Get
                path("shows/$traktId/seasons")
                parameter("extended", "full")
            }
        }

    override suspend fun getSeasonEpisodes(
        traktId: Long,
    ): ApiResponse<List<TraktSeasonEpisodesResponse>, ErrorResponse> =
        httpClient.safeRequest(exceptionHandler) {
            url {
                method = HttpMethod.Get
                path("shows/$traktId/seasons")
                parameter("extended", "full,episodes")
            }
        }

    override suspend fun getSeasonDetails(traktId: Long): ApiResponse<TraktShowResponse, ErrorResponse> =
        httpClient.safeRequest(exceptionHandler) {
            url {
                method = HttpMethod.Get
                path("shows/$traktId")
                parameter("extended", "full")
            }
        }
}
