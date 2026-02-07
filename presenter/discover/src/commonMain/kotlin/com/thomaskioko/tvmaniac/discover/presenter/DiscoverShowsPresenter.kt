package com.thomaskioko.tvmaniac.discover.presenter

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.discover.presenter.DefaultDiscoverShowsPresenter.PresenterInstance
import kotlinx.coroutines.flow.StateFlow

public interface DiscoverShowsPresenter {
    public val state: StateFlow<DiscoverViewState>

    public val presenterInstance: PresenterInstance

    public fun dispatch(action: DiscoverShowAction)

    public interface Factory {
        public operator fun invoke(
            componentContext: ComponentContext,
            onNavigateToShowDetails: (id: Long) -> Unit,
            onNavigateToMore: (categoryId: Long) -> Unit,
            onNavigateToEpisode: (showTraktId: Long, episodeId: Long) -> Unit = { _, _ -> },
            onNavigateToSeason: (showTraktId: Long, seasonId: Long, seasonNumber: Long) -> Unit = { _, _, _ -> },
            onNavigateToUpNext: () -> Unit = {},
        ): DiscoverShowsPresenter
    }
}
