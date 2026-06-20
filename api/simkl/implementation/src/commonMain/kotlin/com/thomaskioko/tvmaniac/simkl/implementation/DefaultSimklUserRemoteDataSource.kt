package com.thomaskioko.tvmaniac.simkl.implementation

import com.thomaskioko.tvmaniac.core.base.SimklApi
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.authSafeRequest
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.simkl.api.SimklUserRemoteDataSource
import com.thomaskioko.tvmaniac.simkl.api.model.SimklUserSettingsResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklUserStatsResponse
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.http.path

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultSimklUserRemoteDataSource(
    @SimklApi private val httpClient: HttpClient,
) : SimklUserRemoteDataSource {

    override suspend fun getUserSettings(): ApiResponse<SimklUserSettingsResponse> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Post
                path("users/settings")
            }
        }

    override suspend fun getUserStats(userId: Long): ApiResponse<SimklUserStatsResponse> =
        httpClient.authSafeRequest {
            url {
                method = HttpMethod.Post
                path("users/$userId/stats")
            }
        }
}
