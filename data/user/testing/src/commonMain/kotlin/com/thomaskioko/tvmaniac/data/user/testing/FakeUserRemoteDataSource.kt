package com.thomaskioko.tvmaniac.data.user.testing

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.data.user.api.UserRemoteDataSource
import com.thomaskioko.tvmaniac.data.user.api.model.RemoteUserProfile
import com.thomaskioko.tvmaniac.data.user.api.model.RemoteUserStats

public class FakeUserRemoteDataSource(
    override val provider: AccountProvider = AccountProvider.TRAKT,
    private val profileResponse: ApiResponse<RemoteUserProfile> = ApiResponse.Success(
        RemoteUserProfile(
            slug = "test-user",
            username = "testuser",
            fullName = "Test User",
            avatarUrl = "https://example.com/avatar.jpg",
            backgroundUrl = null,
        ),
    ),
    private val statsResponse: ApiResponse<RemoteUserStats?> = ApiResponse.Success(null),
) : UserRemoteDataSource {

    override suspend fun getUserProfile(userId: String): ApiResponse<RemoteUserProfile> = profileResponse

    override suspend fun getUserStats(userId: String): ApiResponse<RemoteUserStats?> = statsResponse
}
