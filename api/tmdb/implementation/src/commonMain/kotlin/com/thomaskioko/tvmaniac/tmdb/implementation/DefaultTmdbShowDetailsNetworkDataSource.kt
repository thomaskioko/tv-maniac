package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.safeRequest
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowDetailsResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowResult
import com.thomaskioko.tvmaniac.tmdb.api.model.WatchProvidersResult
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import io.ktor.client.request.parameter
import io.ktor.http.HttpMethod
import io.ktor.http.path

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTmdbShowDetailsNetworkDataSource(
    private val httpClient: TmdbHttpClient,
) : TmdbShowDetailsNetworkDataSource {

    override suspend fun getShowDetails(id: Long): ApiResponse<TmdbShowDetailsResponse> {
        return httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("3/tv/$id")
                parameter("append_to_response", "credits,videos")
            }
        }
    }

    override suspend fun getSimilarShows(id: Long, page: Long): ApiResponse<TmdbShowResult> {
        return httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("3/tv/$id/similar")
                parameter("page", "$page")
            }
        }
    }

    override suspend fun getRecommendedShows(id: Long, page: Long): ApiResponse<TmdbShowResult> {
        return httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("3/tv/$id/recommendations")
                parameter("page", "$page")
            }
        }
    }

    override suspend fun getShowWatchProviders(id: Long): ApiResponse<WatchProvidersResult> {
        return httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("3/tv/$id/watch/providers")
            }
        }
    }
}
