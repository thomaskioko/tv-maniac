package com.thomaskioko.trakt.service.implementation.api

import com.thomaskioko.trakt.service.implementation.TraktHttpClient
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.authSafeRequest
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.TraktListRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddRemoveShowFromListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddShowRequest
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddShowToListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktCreateListRequest
import com.thomaskioko.tvmaniac.trakt.api.model.TraktCreateListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktFollowedShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktPersonalListsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShow
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowIds
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserResponse
import io.ktor.client.request.parameter
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.path
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTraktListRemoteDataSource(
    private val httpClient: TraktHttpClient,
) : TraktListRemoteDataSource {

    override suspend fun getUser(userId: String): ApiResponse<TraktUserResponse> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Get
                path("users/$userId")
                parameter("extended", "full")
            }
        }

    override suspend fun getUserList(userId: String): ApiResponse<List<TraktPersonalListsResponse>> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Get
                path("users/$userId/lists")
            }
        }

    override suspend fun createFollowingList(userSlug: String): ApiResponse<TraktCreateListResponse> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Post
                path("users/$userSlug/lists")
            }
            contentType(ContentType.Application.Json)
            setBody(TraktCreateListRequest())
        }

    override suspend fun createList(userSlug: String, name: String): ApiResponse<TraktCreateListResponse> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Post
                path("users/$userSlug/lists")
            }
            contentType(ContentType.Application.Json)
            setBody(TraktCreateListRequest(name = name))
        }

    override suspend fun getFollowedList(
        listId: Long,
        userSlug: String,
    ): ApiResponse<List<TraktFollowedShowResponse>> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Get
                path("users/$userSlug/lists/$listId/items/shows")
                parameter("sort_by", "added")
            }
        }

    override suspend fun getWatchList(sortBy: String, sortHow: String): ApiResponse<List<TraktFollowedShowResponse>> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Get
                path("users/me/watchlist/shows")
                parameter("limit", "10000")
            }
            headers.append("X-Sort-By", sortBy)
            headers.append("X-Sort-How", sortHow)
        }

    override suspend fun addShowToWatchListByTmdbId(
        tmdbId: Long,
    ): ApiResponse<TraktAddShowToListResponse> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Post
                path("sync/watchlist")
            }
            contentType(ContentType.Application.Json)
            setBody(
                TraktAddShowRequest(
                    shows = listOf(TraktShow(ids = TraktShowIds(tmdbId = tmdbId))),
                ),
            )
        }

    override suspend fun removeShowFromWatchListByTmdbId(
        tmdbId: Long,
    ): ApiResponse<TraktAddRemoveShowFromListResponse> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Post
                path("sync/watchlist/remove")
            }
            contentType(ContentType.Application.Json)
            setBody(
                TraktAddShowRequest(
                    shows = listOf(TraktShow(ids = TraktShowIds(tmdbId = tmdbId))),
                ),
            )
        }

    override suspend fun addShowToWatchListByTraktId(
        traktId: Long,
    ): ApiResponse<TraktAddShowToListResponse> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Post
                path("sync/watchlist")
            }
            contentType(ContentType.Application.Json)
            setBody(
                TraktAddShowRequest(
                    shows = listOf(TraktShow(ids = TraktShowIds(traktId = traktId))),
                ),
            )
        }

    override suspend fun removeShowFromWatchListByTraktId(
        traktId: Long,
    ): ApiResponse<TraktAddRemoveShowFromListResponse> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Post
                path("sync/watchlist/remove")
            }
            contentType(ContentType.Application.Json)
            setBody(
                TraktAddShowRequest(
                    shows = listOf(TraktShow(ids = TraktShowIds(traktId = traktId))),
                ),
            )
        }

    override suspend fun addShowToList(
        userSlug: String,
        listId: Long,
        traktShowId: Long,
    ): ApiResponse<TraktAddShowToListResponse> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Post
                path("users/$userSlug/lists/$listId/items")
            }
            contentType(ContentType.Application.Json)
            setBody(
                TraktAddShowRequest(
                    shows = listOf(
                        TraktShow(
                            ids = TraktShowIds(
                                traktId = traktShowId,
                            ),
                        ),
                    ),
                ),
            )
        }

    override suspend fun removeShowFromList(
        userSlug: String,
        listId: Long,
        traktShowId: Long,
    ): ApiResponse<TraktAddRemoveShowFromListResponse> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Post
                path("users/$userSlug/lists/$listId/items/remove")
            }
            contentType(ContentType.Application.Json)
            setBody(
                TraktAddShowRequest(
                    shows = listOf(
                        TraktShow(
                            ids = TraktShowIds(
                                traktId = traktShowId,
                            ),
                        ),
                    ),
                ),
            )
        }
}
