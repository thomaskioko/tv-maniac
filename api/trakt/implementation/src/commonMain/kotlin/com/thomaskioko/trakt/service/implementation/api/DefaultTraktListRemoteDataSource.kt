package com.thomaskioko.trakt.service.implementation.api

import com.thomaskioko.trakt.service.implementation.TraktHttpClient
import com.thomaskioko.tvmaniac.core.networkutil.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.model.safeRequest
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
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("users/$userId")
                parameter("extended", "full")
            }
        }

    override suspend fun getUserList(userId: String): List<TraktPersonalListsResponse> =
        httpClient.get("users/$userId/lists").body()

    override suspend fun createFollowingList(userSlug: String): TraktCreateListResponse =
        httpClient.post("users/$userSlug/lists") { setBody(TraktCreateListRequest()) }.body()

    override suspend fun getFollowedList(
        listId: Long,
        userSlug: String,
    ): List<TraktFollowedShowResponse> =
        httpClient
            .get("users/$userSlug/lists/$listId/items/shows") { parameter("sort_by", "added") }
            .body()

    override suspend fun getWatchList(): ApiResponse<List<TraktFollowedShowResponse>> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("sync/watchlist/shows")
                parameter("sort_by", "added")
            }
        }

    override suspend fun addShowToWatchListByTmdbId(
        tmdbId: Long,
    ): ApiResponse<TraktAddShowToListResponse> =
        httpClient.safeRequest {
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
        httpClient.safeRequest {
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

    override suspend fun addShowToList(
        userSlug: String,
        listId: Long,
        traktShowId: Long,
    ): TraktAddShowToListResponse =
        httpClient
            .post("users/$userSlug/lists/$listId/items") {
                setBody(
                    TraktAddShowRequest(
                        shows =
                        listOf(
                            TraktShow(
                                ids =
                                TraktShowIds(
                                    traktId = traktShowId,
                                ),
                            ),
                        ),
                    ),
                )
            }
            .body()
}
