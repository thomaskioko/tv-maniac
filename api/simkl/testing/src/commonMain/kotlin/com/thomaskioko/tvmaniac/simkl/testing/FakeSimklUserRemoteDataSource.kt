package com.thomaskioko.tvmaniac.simkl.testing

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.simkl.api.SimklUserRemoteDataSource
import com.thomaskioko.tvmaniac.simkl.api.model.SimklUserSettingsResponse

public class FakeSimklUserRemoteDataSource(
    private var userSettingsResponse: ApiResponse<SimklUserSettingsResponse> = ApiResponse.Unauthenticated,
) : SimklUserRemoteDataSource {

    public fun setUserSettings(response: ApiResponse<SimklUserSettingsResponse>) {
        userSettingsResponse = response
    }

    override suspend fun getUserSettings(): ApiResponse<SimklUserSettingsResponse> =
        userSettingsResponse
}
