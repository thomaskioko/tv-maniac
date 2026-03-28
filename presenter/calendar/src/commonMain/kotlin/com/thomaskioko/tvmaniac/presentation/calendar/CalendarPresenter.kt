package com.thomaskioko.tvmaniac.presentation.calendar

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow

public interface CalendarPresenter {
    public val state: StateFlow<CalendarState>
    public fun dispatch(action: CalendarAction)

    public interface Factory {
        public operator fun invoke(
            componentContext: ComponentContext,
            navigateToShowDetails: (showId: Long) -> Unit,
            onEpisodeLongPressed: (episodeId: Long) -> Unit = {},
        ): CalendarPresenter
    }
}
