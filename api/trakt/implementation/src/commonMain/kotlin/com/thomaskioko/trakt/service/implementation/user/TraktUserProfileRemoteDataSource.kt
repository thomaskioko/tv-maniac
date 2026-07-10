package com.thomaskioko.trakt.service.implementation.user

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.map
import com.thomaskioko.tvmaniac.data.user.api.UserRemoteDataSource
import com.thomaskioko.tvmaniac.data.user.api.model.RemoteUserProfile
import com.thomaskioko.tvmaniac.data.user.api.model.RemoteUserStats
import com.thomaskioko.tvmaniac.trakt.api.TraktUserRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserStatsResponse
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesIntoSet(AppScope::class)
public class TraktUserProfileRemoteDataSource(
    private val remoteDataSource: TraktUserRemoteDataSource,
) : UserRemoteDataSource {

    override val provider: SyncProviderSource = SyncProviderSource.TRAKT

    override suspend fun getUserProfile(userId: String): ApiResponse<RemoteUserProfile> =
        remoteDataSource.getUser(userId).map { it.toRemoteUserProfile() }

    override suspend fun getUserStats(userId: String): ApiResponse<RemoteUserStats?> =
        remoteDataSource.getUserStats(userId).map { it.toRemoteUserStats() }
}

private fun TraktUserResponse.toRemoteUserProfile(): RemoteUserProfile = RemoteUserProfile(
    slug = ids.slug,
    username = userName,
    fullName = name,
    avatarUrl = images.avatar.full,
    backgroundUrl = null,
)

private fun TraktUserStatsResponse.toRemoteUserStats(): RemoteUserStats = RemoteUserStats(
    showsWatched = shows.watched.toLong(),
    episodesWatched = episodes.watched.toLong(),
    minutesWatched = (episodes.minutes + movies.minutes).toLong(),
)
