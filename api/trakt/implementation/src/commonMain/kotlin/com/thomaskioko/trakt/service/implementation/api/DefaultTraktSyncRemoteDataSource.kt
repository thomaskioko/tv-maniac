package com.thomaskioko.trakt.service.implementation.api

import com.thomaskioko.tvmaniac.core.base.TraktApi
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.authSafeRequest
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.TraktSyncRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktLastActivitiesResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktPlaybackEpisodeResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUpNextNitroResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedProgressResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedShowResponse
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.http.path

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTraktSyncRemoteDataSource(
    @TraktApi
    private val httpClient: HttpClient,
) : TraktSyncRemoteDataSource {

    override suspend fun getLastActivities(): ApiResponse<TraktLastActivitiesResponse> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Get
                path("sync/last_activities")
            }
        }

    override suspend fun getPlaybackEpisodes(
        limit: Int,
    ): ApiResponse<List<TraktPlaybackEpisodeResponse>> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Get
                path("sync/playback/episodes")
                parameters.append("limit", limit.toString())
            }
        }

    override suspend fun getShowWatchedProgress(
        traktId: Long,
        lastActivity: String?,
        hidden: Boolean,
        specials: Boolean,
    ): ApiResponse<TraktWatchedProgressResponse> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Get
                path("shows/$traktId/progress/watched")
                lastActivity?.let { parameters.append("last_activity", it) }
                parameters.append("hidden", hidden.toString())
                parameters.append("specials", specials.toString())
            }
        }

    override suspend fun getUpNextNitro(
        intent: String,
        limit: Int,
        page: Int,
    ): ApiResponse<List<TraktUpNextNitroResponse>> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Get
                path("sync/progress/up_next_nitro")
                parameters.append("intent", intent)
                parameters.append("limit", limit.toString())
                parameters.append("page", page.toString())
            }
        }

    override suspend fun getWatchedShows(
        page: Int,
        limit: Int,
        extended: String,
    ): ApiResponse<List<TraktWatchedShowResponse>> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Get
                path("sync/watched/shows")
                parameters.append("extended", extended)
                parameters.append("page", page.toString())
                parameters.append("limit", limit.toString())
            }
        }
}
