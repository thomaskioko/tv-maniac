package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.core.networkutil.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.model.safeRequest
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowDetailsResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowResult
import com.thomaskioko.tvmaniac.tmdb.api.model.WatchProvidersResult
import io.ktor.client.request.parameter
import io.ktor.http.HttpMethod
import io.ktor.http.path
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
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
