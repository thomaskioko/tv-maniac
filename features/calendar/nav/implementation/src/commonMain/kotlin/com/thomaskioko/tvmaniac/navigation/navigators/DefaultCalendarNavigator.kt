package com.thomaskioko.tvmaniac.navigation.navigators

import com.thomaskioko.root.model.ScreenSource
import com.thomaskioko.root.nav.EpisodeSheetNavigator
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.presentation.calendar.CalendarNavigator
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(ActivityScope::class)
public class DefaultCalendarNavigator(
    private val episodeSheetNavigator: EpisodeSheetNavigator,
) : CalendarNavigator {
    override fun showEpisodeSheet(episodeId: Long) {
        episodeSheetNavigator.showEpisodeSheet(episodeId, ScreenSource.CALENDAR)
    }
}
