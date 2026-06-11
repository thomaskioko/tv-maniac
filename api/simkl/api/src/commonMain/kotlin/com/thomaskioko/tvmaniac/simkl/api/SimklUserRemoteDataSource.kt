package com.thomaskioko.tvmaniac.simkl.api

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklUserSettingsResponse

public interface SimklUserRemoteDataSource {
    public suspend fun getUserSettings(): ApiResponse<SimklUserSettingsResponse>
}
