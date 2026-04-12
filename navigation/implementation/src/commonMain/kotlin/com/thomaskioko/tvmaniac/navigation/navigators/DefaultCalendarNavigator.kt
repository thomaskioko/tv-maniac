package com.thomaskioko.tvmaniac.navigation.navigators

import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.EpisodeSheetController
import com.thomaskioko.tvmaniac.presentation.calendar.CalendarNavigator
import com.thomaskioko.tvmaniac.navigation.model.ScreenSource
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(ActivityScope::class)
public class DefaultCalendarNavigator(
    private val episodeSheetController: EpisodeSheetController,
) : CalendarNavigator {
    override fun showEpisodeSheet(episodeId: Long) {
        episodeSheetController.showEpisodeSheet(episodeId, ScreenSource.CALENDAR)
    }
}
