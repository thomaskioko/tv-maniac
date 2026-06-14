package com.thomaskioko.tvmaniac.simkl.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.core.base.SimklDataApi
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.safeRequest
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
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
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

@SingleIn(AppScope::class)
@ContributesIntoSet(AppScope::class)
public class SimklCalendarRemoteDataSource(
    @SimklDataApi private val httpClient: HttpClient,
    private val followedShowsDao: FollowedShowsDao,
) : CalendarRemoteDataSource {

    override val provider: AccountProvider = AccountProvider.SIMKL

    override suspend fun getCalendarEntries(
        startDate: String,
        days: Int,
    ): ApiResponse<List<RemoteCalendarEntry>> {
        val trackedTmdbIds = followedShowsDao.entries()
            .mapNotNull { it.tmdbId }
            .toSet()

        if (trackedTmdbIds.isEmpty()) return ApiResponse.Success(emptyList())

        return coroutineScope {
            val tvDeferred = async { fetchCalendarFeed("calendar/tv.json") }
            val animeDeferred = async { fetchCalendarFeed("calendar/anime.json") }

            mergeFeedResults(
                tvResult = tvDeferred.await(),
                animeResult = animeDeferred.await(),
                trackedTmdbIds = trackedTmdbIds,
            )
        }
    }

    private fun mergeFeedResults(
        tvResult: ApiResponse<List<SimklCalendarEntry>>,
        animeResult: ApiResponse<List<SimklCalendarEntry>>,
        trackedTmdbIds: Set<Long>,
    ): ApiResponse<List<RemoteCalendarEntry>> {
        if (tvResult is ApiResponse.Unauthenticated) return ApiResponse.Unauthenticated
        if (animeResult is ApiResponse.Unauthenticated) return ApiResponse.Unauthenticated
        if (tvResult !is ApiResponse.Success) return ApiResponse.Success(emptyList())
        if (animeResult !is ApiResponse.Success) return ApiResponse.Success(emptyList())

        val merged = (tvResult.body + animeResult.body)
            .filter { entry ->
                val tmdbId = entry.ids?.tmdb?.toLongOrNull()
                tmdbId != null && tmdbId in trackedTmdbIds
            }
            .mapNotNull { it.toRemoteCalendarEntry() }

        return ApiResponse.Success(merged)
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
