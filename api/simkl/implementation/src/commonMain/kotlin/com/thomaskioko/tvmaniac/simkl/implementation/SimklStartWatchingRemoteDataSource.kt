package com.thomaskioko.tvmaniac.simkl.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.map
import com.thomaskioko.tvmaniac.simkl.api.SimklSyncRemoteDataSource
import com.thomaskioko.tvmaniac.simkl.api.model.SimklWatchedShow
import com.thomaskioko.tvmaniac.startwatching.api.RemotePlanToWatchShow
import com.thomaskioko.tvmaniac.startwatching.api.StartWatchingRemoteDataSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn
import kotlin.time.Instant

@SingleIn(AppScope::class)
@ContributesIntoSet(AppScope::class)
public class SimklStartWatchingRemoteDataSource(
    private val syncRemoteDataSource: SimklSyncRemoteDataSource,
) : StartWatchingRemoteDataSource {

    override val provider: AccountProvider = AccountProvider.SIMKL

    override suspend fun getPlanToWatch(): ApiResponse<List<RemotePlanToWatchShow>> =
        syncRemoteDataSource.getAllWatchedShows()
            .map { response ->
                response.shows
                    .filter { it.status == PLAN_TO_WATCH_STATUS }
                    .map { it.toRemotePlanToWatchShow() }
            }

    private companion object {
        private const val PLAN_TO_WATCH_STATUS = "plantowatch"
    }
}

private fun SimklWatchedShow.toRemotePlanToWatchShow(): RemotePlanToWatchShow = RemotePlanToWatchShow(
    tmdbId = show.ids.tmdb?.toLongOrNull(),
    imdbId = show.ids.imdb,
    providerShowId = show.ids.simkl?.toString(),
    provider = AccountProvider.SIMKL,
    title = show.title ?: "",
    year = show.year,
    followedAt = lastWatchedAt?.let { runCatching { Instant.parse(it) }.getOrNull() }
        ?: Instant.fromEpochSeconds(0),
)
