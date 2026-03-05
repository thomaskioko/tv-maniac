package com.thomaskioko.tvmaniac.domain.calendar

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.data.calendar.CalendarEntry
import com.thomaskioko.tvmaniac.data.calendar.CalendarRepository
import com.thomaskioko.tvmaniac.domain.calendar.model.GroupedCalendarEntry
import com.thomaskioko.tvmaniac.domain.calendar.model.GroupedEpisodeEntry
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import me.tatarka.inject.annotations.Inject
import kotlin.time.Instant

@Inject
public class ObserveCalendarInteractor(
    private val repository: CalendarRepository,
    private val calendarWeekCalculator: CalendarWeekCalculator,
    private val calendarEpisodeFormatter: CalendarEpisodeFormatter,
    private val dateTimeProvider: DateTimeProvider,
) : SubjectInteractor<ObserveCalendarInteractor.Params, List<GroupedCalendarEntry>>() {

    override fun createObservable(params: Params): Flow<List<GroupedCalendarEntry>> {
        return repository.observeCalendarEntries(params.startDate, params.endDate)
            .map { entries -> groupEntriesByDate(entries) }
    }

    private fun groupEntriesByDate(entries: List<CalendarEntry>): List<GroupedCalendarEntry> {
        val timeZone = TimeZone.currentSystemDefault()
        val today = dateTimeProvider.now().toLocalDateTime(timeZone).date
        val tomorrow = today.plus(1, DateTimeUnit.DAY)

        return entries
            .groupBy { entry ->
                Instant.fromEpochMilliseconds(entry.airDate)
                    .toLocalDateTime(timeZone)
                    .date
            }
            .entries
            .sortedBy { it.key }
            .map { (date, dateEntries) ->
                val dateLabel = calendarWeekCalculator.formatDateLabel(date, today, tomorrow)
                val episodeItems = groupEntriesByShow(dateEntries)
                GroupedCalendarEntry(
                    dateLabel = dateLabel,
                    episodes = episodeItems,
                )
            }
    }

    private fun groupEntriesByShow(entries: List<CalendarEntry>): List<GroupedEpisodeEntry> {
        return entries
            .groupBy { it.showTraktId }
            .map { (_, showEntries) ->
                val sortedEntries = showEntries.sortedWith(
                    compareBy({ it.seasonNumber }, { it.episodeNumber }),
                )
                val firstEntry = sortedEntries.first()
                val additionalCount = sortedEntries.size - 1

                GroupedEpisodeEntry(
                    showTraktId = firstEntry.showTraktId,
                    episodeTraktId = firstEntry.episodeTraktId,
                    showTitle = firstEntry.showTitle,
                    posterUrl = firstEntry.showPosterPath,
                    episodeInfo = calendarEpisodeFormatter.formatEpisodeInfo(
                        seasonNumber = firstEntry.seasonNumber,
                        episodeNumber = firstEntry.episodeNumber,
                        episodeTitle = firstEntry.episodeTitle,
                    ),
                    airTime = calendarEpisodeFormatter.formatAirTime(firstEntry.airDate),
                    network = firstEntry.network,
                    additionalEpisodesCount = additionalCount,
                    overview = firstEntry.overview,
                    rating = firstEntry.rating,
                    votes = firstEntry.votes,
                    runtime = firstEntry.runtime,
                    formattedAirDate = calendarEpisodeFormatter.formatFullAirDate(firstEntry.airDate),
                )
            }
            .sortedBy { it.airTime }
    }

    public data class Params(
        val startDate: Long,
        val endDate: Long,
    )
}
