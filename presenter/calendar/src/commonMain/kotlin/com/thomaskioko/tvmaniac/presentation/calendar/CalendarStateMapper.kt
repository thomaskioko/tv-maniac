package com.thomaskioko.tvmaniac.presentation.calendar

import com.thomaskioko.tvmaniac.domain.calendar.model.DateLabel
import com.thomaskioko.tvmaniac.domain.calendar.model.GroupedCalendarEntry
import com.thomaskioko.tvmaniac.domain.calendar.model.GroupedEpisodeEntry
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.presentation.calendar.model.CalendarDateGroup
import com.thomaskioko.tvmaniac.presentation.calendar.model.CalendarEpisodeItem
import dev.zacsweers.metro.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Inject
public class CalendarStateMapper(
    private val localizer: Localizer,
) {

    public fun getString(key: StringResourceKey): String = localizer.getString(key)

    public fun toCalendarDateGroups(
        entries: List<GroupedCalendarEntry>,
    ): ImmutableList<CalendarDateGroup> {
        return entries.map { group ->
            CalendarDateGroup(
                dateLabel = resolveDateLabel(group.dateLabel),
                episodes = toCalendarEpisodeItems(group.episodes),
            )
        }.toImmutableList()
    }

    private fun resolveDateLabel(dateLabel: DateLabel): String {
        return when (dateLabel) {
            is DateLabel.Today -> {
                val today = localizer.getString(StringResourceKey.LabelCalendarToday)
                "$today, ${dateLabel.formattedDate}"
            }
            is DateLabel.Tomorrow -> {
                val tomorrow = localizer.getString(StringResourceKey.LabelCalendarTomorrow)
                "$tomorrow, ${dateLabel.formattedDate}"
            }
            is DateLabel.DayOfWeek -> "${dateLabel.dayName}, ${dateLabel.formattedDate}"
        }
    }

    private fun toCalendarEpisodeItems(
        entries: List<GroupedEpisodeEntry>,
    ): ImmutableList<CalendarEpisodeItem> {
        return entries.map { entry ->
            CalendarEpisodeItem(
                showTraktId = entry.showTraktId,
                episodeTraktId = entry.episodeTraktId,
                showTitle = entry.showTitle,
                posterUrl = entry.posterUrl,
                episodeInfo = entry.episodeInfo,
                airTime = entry.airTime,
                network = entry.network,
                additionalEpisodesCount = entry.additionalEpisodesCount,
                overview = entry.overview,
                rating = entry.rating,
                votes = entry.votes,
                runtime = entry.runtime,
                formattedAirDate = entry.formattedAirDate,
            )
        }.toImmutableList()
    }
}
