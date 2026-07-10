package com.thomaskioko.tvmaniac.simkl.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.map
import com.thomaskioko.tvmaniac.simkl.api.SimklSyncRemoteDataSource
import com.thomaskioko.tvmaniac.simkl.api.model.SimklLastActivitiesResponse
import com.thomaskioko.tvmaniac.syncactivity.api.RemoteActivitySource
import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn
import kotlin.time.Instant

@SingleIn(AppScope::class)
@ContributesIntoSet(AppScope::class)
public class SimklRemoteActivitySource(
    private val remoteDataSource: SimklSyncRemoteDataSource,
) : RemoteActivitySource {

    override val provider: SyncProviderSource = SyncProviderSource.SIMKL

    override suspend fun getLastActivities(): ApiResponse<Map<ActivityType, Instant>> =
        remoteDataSource.getLastActivities().map { it.toActivityMap() }
}

private fun SimklLastActivitiesResponse.toActivityMap(): Map<ActivityType, Instant> = buildMap {
    val shows = tvShows ?: return@buildMap
    listOfNotNull(
        shows.planToWatch?.parseInstant(),
        shows.watching?.parseInstant(),
        shows.hold?.parseInstant(),
    ).maxOrNull()?.let { put(ActivityType.SHOWS_WATCHLISTED, it) }
    shows.all?.parseInstant()?.let { put(ActivityType.EPISODES_WATCHED, it) }
}

private fun String.parseInstant(): Instant? =
    try {
        Instant.parse(this)
    } catch (_: IllegalArgumentException) {
        null
    }
