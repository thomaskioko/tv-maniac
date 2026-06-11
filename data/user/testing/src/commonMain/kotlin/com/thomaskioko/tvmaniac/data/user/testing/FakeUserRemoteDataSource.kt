package com.thomaskioko.tvmaniac.data.user.testing

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.data.user.api.UserRemoteDataSource
import com.thomaskioko.tvmaniac.data.user.api.model.RemoteUserProfile
import com.thomaskioko.tvmaniac.data.user.api.model.RemoteUserStats

public class FakeUserRemoteDataSource(
    override var provider: AccountProvider = AccountProvider.TRAKT,
) : UserRemoteDataSource {

    private var profileResponse: ApiResponse<RemoteUserProfile> = ApiResponse.Success(
        RemoteUserProfile(
            slug = "test-user",
            username = "testuser",
            fullName = "Test User",
            avatarUrl = "https://example.com/avatar.jpg",
            backgroundUrl = null,
        ),
    )
    private var statsResponse: ApiResponse<RemoteUserStats?> = ApiResponse.Success(null)

    public fun setProfileResponse(response: ApiResponse<RemoteUserProfile>) {
        profileResponse = response
    }

    public fun setStatsResponse(response: ApiResponse<RemoteUserStats?>) {
        statsResponse = response
    }

    override suspend fun getUserProfile(userId: String): ApiResponse<RemoteUserProfile> = profileResponse

    override suspend fun getUserStats(userId: String): ApiResponse<RemoteUserStats?> = statsResponse
}
