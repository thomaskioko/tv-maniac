package com.thomaskioko.tvmaniac.data.calendar.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.calendar.CalendarDao
import com.thomaskioko.tvmaniac.data.calendar.CalendarEntry
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.ObserveEntriesBetweenDates
import com.thomaskioko.tvmaniac.db.TraktId
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultCalendarDao(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : CalendarDao {

    override fun observeEntriesBetweenDates(startDate: Long, endDate: Long): Flow<List<CalendarEntry>> =
        database.calendarQueries
            .observeEntriesBetweenDates(startDate, endDate)
            .asFlow()
            .mapToList(dispatchers.io)
            .map { entries -> entries.map { it.toCalendarEntry() } }

    override fun hasEntriesInRange(startDate: Long, endDate: Long): Boolean =
        database.calendarQueries.hasEntriesInRange(startDate, endDate).executeAsOne()

    override fun upsert(entry: CalendarEntry) {
        database.calendarQueries.upsert(
            show_trakt_id = Id<TraktId>(entry.showTraktId),
            episode_trakt_id = entry.episodeTraktId,
            season_number = entry.seasonNumber.toLong(),
            episode_number = entry.episodeNumber.toLong(),
            episode_title = entry.episodeTitle,
            air_date = entry.airDate,
            show_title = entry.showTitle,
            show_poster_path = entry.showPosterPath,
            network = entry.network,
            runtime = entry.runtime?.toLong(),
            overview = entry.overview,
            rating = entry.rating,
            votes = entry.votes?.toLong(),
        )
    }

    override fun deleteEntriesInRange(startDate: Long, endDate: Long) {
        database.calendarQueries.deleteEntriesInRange(startDate, endDate)
    }

    override fun deleteOldEntries(cutoffDate: Long) {
        database.calendarQueries.deleteOldEntries(cutoffDate)
    }

    override fun deleteAll() {
        database.calendarQueries.deleteAll()
    }
}

private fun ObserveEntriesBetweenDates.toCalendarEntry(): CalendarEntry = CalendarEntry(
    showTraktId = show_trakt_id.id,
    episodeTraktId = episode_trakt_id,
    seasonNumber = season_number.toInt(),
    episodeNumber = episode_number.toInt(),
    episodeTitle = episode_title,
    airDate = air_date,
    showTitle = show_title,
    showPosterPath = show_poster_path,
    network = network,
    runtime = runtime?.toInt(),
    overview = overview,
    rating = rating,
    votes = votes?.toInt(),
)
