package com.thomaskioko.trakt.service.implementation.sync

import com.thomaskioko.tvmaniac.connectedaccount.api.ConnectedProvider
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.map
import com.thomaskioko.tvmaniac.syncactivity.api.RemoteActivitySource
import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType
import com.thomaskioko.tvmaniac.trakt.api.TraktSyncRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktLastActivitiesResponse
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn
import kotlin.time.Instant

@SingleIn(AppScope::class)
@ContributesIntoSet(AppScope::class)
public class TraktRemoteActivitySource(
    private val remoteDataSource: TraktSyncRemoteDataSource,
) : RemoteActivitySource {

    override val provider: ConnectedProvider = ConnectedProvider.TRAKT

    override suspend fun getLastActivities(): ApiResponse<Map<ActivityType, Instant>> =
        remoteDataSource.getLastActivities().map { it.toActivityMap() }
}

private fun TraktLastActivitiesResponse.toActivityMap(): Map<ActivityType, Instant> = buildMap {
    shows.watchlistedAt?.parseInstant()?.let { put(ActivityType.SHOWS_WATCHLISTED, it) }
    shows.favoritedAt?.parseInstant()?.let { put(ActivityType.SHOWS_FAVORITED, it) }
    episodes.watchedAt?.parseInstant()?.let { put(ActivityType.EPISODES_WATCHED, it) }
    episodes.pausedAt?.parseInstant()?.let { put(ActivityType.EPISODES_PAUSED, it) }
}

private fun String.parseInstant(): Instant? =
    try {
        Instant.parse(this)
    } catch (_: IllegalArgumentException) {
        null
    }
