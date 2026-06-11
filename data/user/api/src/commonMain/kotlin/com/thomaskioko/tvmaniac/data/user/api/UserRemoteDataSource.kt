package com.thomaskioko.tvmaniac.data.user.api

import com.thomaskioko.tvmaniac.accountmanager.api.ProviderScoped
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.data.user.api.model.RemoteUserProfile
import com.thomaskioko.tvmaniac.data.user.api.model.RemoteUserStats

public interface UserRemoteDataSource : ProviderScoped {

    public suspend fun getUserProfile(userId: String): ApiResponse<RemoteUserProfile>

    public suspend fun getUserStats(userId: String): ApiResponse<RemoteUserStats?>
}
