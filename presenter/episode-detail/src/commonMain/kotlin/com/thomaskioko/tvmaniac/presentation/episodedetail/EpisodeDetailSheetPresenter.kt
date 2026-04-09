package com.thomaskioko.tvmaniac.presentation.episodedetail

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.flow.StateFlow

public interface EpisodeDetailSheetPresenter {
    public val state: StateFlow<EpisodeDetailSheetState>
    public val stateValue: Value<EpisodeDetailSheetState>
    public fun dispatch(action: EpisodeDetailSheetAction)

    public interface Factory {
        public operator fun invoke(
            componentContext: ComponentContext,
            episodeId: Long,
            source: ScreenSource,
            navigateToShowDetails: (showTraktId: Long) -> Unit,
            navigateToSeasonDetails: (showTraktId: Long, seasonId: Long, seasonNumber: Long) -> Unit,
            dismissSheet: () -> Unit,
        ): EpisodeDetailSheetPresenter
    }
}
