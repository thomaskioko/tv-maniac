package com.thomaskioko.tvmaniac.presentation.upnext

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.flow.StateFlow

public interface UpNextPresenter {
    public val state: StateFlow<UpNextState>
    public val stateValue: Value<UpNextState>
    public fun dispatch(action: UpNextAction)

    public interface Factory {
        public operator fun invoke(
            componentContext: ComponentContext,
            navigateToShowDetails: (showTraktId: Long) -> Unit,
            navigateToSeasonDetails: (showTraktId: Long, seasonId: Long, seasonNumber: Long) -> Unit,
            onEpisodeLongPressed: (episodeId: Long) -> Unit = {},
        ): UpNextPresenter
    }
}
