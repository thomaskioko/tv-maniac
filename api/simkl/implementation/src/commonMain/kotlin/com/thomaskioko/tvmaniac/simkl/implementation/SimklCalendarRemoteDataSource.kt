package com.thomaskioko.tvmaniac.simkl.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.core.base.SimklDataApi
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.safeRequest
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.map
import com.thomaskioko.tvmaniac.data.calendar.CalendarRemoteDataSource
import com.thomaskioko.tvmaniac.data.calendar.RemoteCalendarEntry
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsDao
import com.thomaskioko.tvmaniac.simkl.api.model.SimklCalendarEntry
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.http.path

@SingleIn(AppScope::class)
@ContributesIntoSet(AppScope::class)
public class SimklCalendarRemoteDataSource(
    @SimklDataApi private val httpClient: HttpClient,
    private val followedShowsDao: FollowedShowsDao,
) : CalendarRemoteDataSource {

    override val provider: SyncProviderSource = SyncProviderSource.SIMKL

    override suspend fun getCalendarEntries(
        startDate: String,
        days: Int,
    ): ApiResponse<List<RemoteCalendarEntry>> {
        val trackedTmdbIds = followedShowsDao.entries()
            .mapNotNull { it.tmdbId }
            .toSet()

        if (trackedTmdbIds.isEmpty()) return ApiResponse.Success(emptyList())

        return fetchCalendarFeed("calendar/tv.json").map { entries ->
            entries
                .filter { entry ->
                    val tmdbId = entry.ids?.tmdb?.toLongOrNull()
                    tmdbId != null && tmdbId in trackedTmdbIds
                }
                .mapNotNull { it.toRemoteCalendarEntry() }
        }
    }

    private suspend fun fetchCalendarFeed(path: String): ApiResponse<List<SimklCalendarEntry>> =
        httpClient.safeRequest {
            url { path(path) }
            method = HttpMethod.Get
        }
}

private fun SimklCalendarEntry.toRemoteCalendarEntry(): RemoteCalendarEntry? {
    val tmdbId = ids?.tmdb?.toLongOrNull() ?: return null
    val episodeNumber = episode?.episode ?: return null
    return RemoteCalendarEntry(
        tmdbId = tmdbId,
        episodeTraktId = null,
        showTitle = title ?: "",
        episodeTitle = null,
        seasonNumber = episode?.season ?: 1,
        episodeNumber = episodeNumber,
        firstAiredIso = date,
        runtime = null,
        overview = null,
        rating = null,
        votes = null,
    )
}
