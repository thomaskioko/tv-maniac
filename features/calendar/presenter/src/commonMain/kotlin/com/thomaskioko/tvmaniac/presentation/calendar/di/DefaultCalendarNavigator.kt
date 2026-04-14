package com.thomaskioko.tvmaniac.presentation.calendar.di

import com.thomaskioko.root.model.ScreenSource
import com.thomaskioko.root.nav.EpisodeSheetNavigator
import com.thomaskioko.tvmaniac.calendar.nav.CalendarNavigator
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(ActivityScope::class)
public class DefaultCalendarNavigator(
    private val episodeSheetNavigator: EpisodeSheetNavigator,
) : CalendarNavigator {
    override fun showEpisodeSheet(episodeId: Long) {
        episodeSheetNavigator.showEpisodeSheet(episodeId, ScreenSource.CALENDAR)
    }
}
