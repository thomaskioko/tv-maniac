package com.thomaskioko.trakt.service.implementation.sync

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.map
import com.thomaskioko.tvmaniac.data.calendar.CalendarRemoteDataSource
import com.thomaskioko.tvmaniac.data.calendar.RemoteCalendarEntry
import com.thomaskioko.tvmaniac.trakt.api.TraktCalendarRemoteDataSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesIntoSet(AppScope::class)
public class TraktCalendarRemoteDataSourceAdapter(
    private val traktCalendarDataSource: TraktCalendarRemoteDataSource,
) : CalendarRemoteDataSource {

    override val provider: AccountProvider = AccountProvider.TRAKT

    override suspend fun getCalendarEntries(
        startDate: String,
        days: Int,
    ): ApiResponse<List<RemoteCalendarEntry>> =
        traktCalendarDataSource.getMyShowsCalendar(startDate = startDate, days = days)
            .map { responses -> responses.map { it.toRemoteCalendarEntry() } }
}

private fun com.thomaskioko.tvmaniac.trakt.api.model.TraktCalendarResponse.toRemoteCalendarEntry(): RemoteCalendarEntry =
    RemoteCalendarEntry(
        tmdbId = show.ids.tmdb ?: 0L,
        showTitle = show.title,
        episodeTitle = episode.title,
        seasonNumber = episode.seasonNumber,
        episodeNumber = episode.episodeNumber,
        firstAiredIso = firstAired,
        runtime = episode.runtime,
        overview = episode.overview,
        rating = episode.rating,
        votes = episode.votes,
    )
