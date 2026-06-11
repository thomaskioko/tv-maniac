package com.thomaskioko.tvmaniac.simkl.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.map
import com.thomaskioko.tvmaniac.data.user.api.UserRemoteDataSource
import com.thomaskioko.tvmaniac.data.user.api.model.RemoteUserProfile
import com.thomaskioko.tvmaniac.data.user.api.model.RemoteUserStats
import com.thomaskioko.tvmaniac.simkl.api.SimklUserRemoteDataSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesIntoSet(AppScope::class)
public class SimklUserProfileRemoteDataSource(
    private val remoteDataSource: SimklUserRemoteDataSource,
) : UserRemoteDataSource {

    override val provider: AccountProvider = AccountProvider.SIMKL

    override suspend fun getUserProfile(userId: String): ApiResponse<RemoteUserProfile> =
        remoteDataSource.getUserSettings().map { response ->
            RemoteUserProfile(
                slug = response.account.id.toString(),
                username = response.user.name ?: response.account.id.toString(),
                fullName = null,
                avatarUrl = response.user.avatar,
                backgroundUrl = null,
            )
        }

    override suspend fun getUserStats(userId: String): ApiResponse<RemoteUserStats?> =
        ApiResponse.Success(null)
}
