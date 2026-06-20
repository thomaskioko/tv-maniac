package com.thomaskioko.tvmaniac.simkl.testing

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.simkl.api.SimklUserRemoteDataSource
import com.thomaskioko.tvmaniac.simkl.api.model.SimklUserSettingsResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklUserStatsResponse

public class FakeSimklUserRemoteDataSource(
    private var userSettingsResponse: ApiResponse<SimklUserSettingsResponse> = ApiResponse.Unauthenticated,
    private var userStatsResponse: ApiResponse<SimklUserStatsResponse> = ApiResponse.Unauthenticated,
) : SimklUserRemoteDataSource {

    public fun setUserSettings(response: ApiResponse<SimklUserSettingsResponse>) {
        userSettingsResponse = response
    }

    public fun setUserStats(response: ApiResponse<SimklUserStatsResponse>) {
        userStatsResponse = response
    }

    override suspend fun getUserSettings(): ApiResponse<SimklUserSettingsResponse> =
        userSettingsResponse

    override suspend fun getUserStats(userId: Long): ApiResponse<SimklUserStatsResponse> =
        userStatsResponse
}
