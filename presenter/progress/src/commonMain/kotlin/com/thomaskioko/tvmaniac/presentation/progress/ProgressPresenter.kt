package com.thomaskioko.tvmaniac.presentation.progress

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.presentation.calendar.CalendarPresenter
import com.thomaskioko.tvmaniac.presentation.upnext.UpNextPresenter
import kotlinx.coroutines.flow.StateFlow

public interface ProgressPresenter {
    public val state: StateFlow<ProgressState>
    public val upNextPresenter: UpNextPresenter
    public val calendarPresenter: CalendarPresenter
    public fun dispatch(action: ProgressAction)

    public interface Factory {
        public operator fun invoke(
            componentContext: ComponentContext,
            navigateToShowDetails: (showId: Long) -> Unit,
            navigateToSeasonDetails: (showTraktId: Long, seasonId: Long, seasonNumber: Long) -> Unit,
        ): ProgressPresenter
    }
}
