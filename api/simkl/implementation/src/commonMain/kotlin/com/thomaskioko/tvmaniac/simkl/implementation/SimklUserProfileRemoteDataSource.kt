package com.thomaskioko.tvmaniac.simkl.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.map
import com.thomaskioko.tvmaniac.data.user.api.UserRemoteDataSource
import com.thomaskioko.tvmaniac.data.user.api.model.RemoteUserProfile
import com.thomaskioko.tvmaniac.data.user.api.model.RemoteUserStats
import com.thomaskioko.tvmaniac.simkl.api.SimklUserRemoteDataSource
import com.thomaskioko.tvmaniac.simkl.api.model.SimklStatsDomain
import com.thomaskioko.tvmaniac.simkl.api.model.SimklUserStatsResponse
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
        userId.toLongOrNull()
            ?.let { simklId -> remoteDataSource.getUserStats(simklId).map { it.toRemoteUserStats() } }
            ?: ApiResponse.Success(null)
}

private fun SimklUserStatsResponse.toRemoteUserStats(): RemoteUserStats = RemoteUserStats(
    showsWatched = (tv?.totalShowsWatched() ?: 0L) + (anime?.totalShowsWatched() ?: 0L),
    episodesWatched = (tv?.totalEpisodesWatched() ?: 0L) + (anime?.totalEpisodesWatched() ?: 0L),
    minutesWatched = totalMins?.toLong() ?: 0L,
)

private fun SimklStatsDomain.totalShowsWatched(): Long {
    val watchingCount = watching?.count ?: 0
    val completedCount = completed?.count ?: 0
    val holdCount = hold?.count ?: 0
    return (watchingCount + completedCount + holdCount).toLong()
}

private fun SimklStatsDomain.totalEpisodesWatched(): Long {
    val watchingEpisodes = watching?.watchedEpisodesCount ?: 0
    val completedEpisodes = completed?.watchedEpisodesCount ?: 0
    val holdEpisodes = hold?.watchedEpisodesCount ?: 0
    return (watchingEpisodes + completedEpisodes + holdEpisodes).toLong()
}
