package com.thomaskioko.tvmaniac.discover.presenter

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.discover.presenter.DefaultDiscoverShowsPresenter.PresenterInstance
import kotlinx.coroutines.flow.StateFlow

interface DiscoverShowsPresenter {
    val state: StateFlow<DiscoverViewState>

    val presenterInstance: PresenterInstance

    fun dispatch(action: DiscoverShowAction)

    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onNavigateToShowDetails: (id: Long) -> Unit,
            onNavigateToMore: (categoryId: Long) -> Unit,
            onNavigateToEpisode: (showId: Long, episodeId: Long) -> Unit = { _, _ -> },
        ): DiscoverShowsPresenter
    }
}
