package com.thomaskioko.trakt.service.implementation.api

import com.thomaskioko.tvmaniac.core.base.TraktApi
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.authSafeRequest
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.safeRequest
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.TraktRatingsRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddRatingsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktRatingResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktRatingsRequest
import com.thomaskioko.tvmaniac.trakt.api.model.TraktRemoveRatingsRequest
import com.thomaskioko.tvmaniac.trakt.api.model.TraktRemoveRatingsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserRatingItem
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.path

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTraktRatingsRemoteDataSource(
    @TraktApi
    private val httpClient: HttpClient,
) : TraktRatingsRemoteDataSource {

    override suspend fun addRatings(request: TraktRatingsRequest): ApiResponse<TraktAddRatingsResponse> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Post
                path("sync/ratings")
            }
            contentType(ContentType.Application.Json)
            setBody(request)
        }

    override suspend fun removeRatings(request: TraktRemoveRatingsRequest): ApiResponse<TraktRemoveRatingsResponse> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Post
                path("sync/ratings/remove")
            }
            contentType(ContentType.Application.Json)
            setBody(request)
        }

    override suspend fun getUserShowRatings(): ApiResponse<List<TraktUserRatingItem>> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Get
                path("sync/ratings/shows")
            }
        }

    override suspend fun getShowCommunityRating(traktId: Long): ApiResponse<TraktRatingResponse> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("shows/$traktId/ratings")
            }
        }

    override suspend fun getSeasonCommunityRating(traktId: Long, seasonNumber: Int): ApiResponse<TraktRatingResponse> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("shows/$traktId/seasons/$seasonNumber/ratings")
            }
        }

    override suspend fun getEpisodeCommunityRating(
        traktId: Long,
        seasonNumber: Int,
        episodeNumber: Int,
    ): ApiResponse<TraktRatingResponse> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("shows/$traktId/seasons/$seasonNumber/episodes/$episodeNumber/ratings")
            }
        }
}
