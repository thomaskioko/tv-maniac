package com.thomaskioko.tvmaniac.simkl.implementation

import com.thomaskioko.tvmaniac.core.base.SimklApi
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.safeRequest
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.simkl.api.SimklShowsRemoteDataSource
import com.thomaskioko.tvmaniac.simkl.api.model.SimklSearchShowResponse
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter
import io.ktor.http.HttpMethod
import io.ktor.http.path

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultSimklShowsRemoteDataSource(
    @SimklApi private val httpClient: HttpClient,
) : SimklShowsRemoteDataSource {

    override suspend fun searchShows(query: String, limit: Int): ApiResponse<List<SimklSearchShowResponse>> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("search/tv")
            }
            parameter("q", query)
            parameter("extended", "full")
            parameter("limit", limit)
        }
}
