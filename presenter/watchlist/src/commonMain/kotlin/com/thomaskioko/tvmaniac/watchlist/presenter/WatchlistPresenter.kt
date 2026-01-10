package com.thomaskioko.tvmaniac.watchlist.presenter

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow

public interface WatchlistPresenter {
    public val state: StateFlow<WatchlistState>
    public fun dispatch(action: WatchlistAction)

    public interface Factory {
        public operator fun invoke(
            componentContext: ComponentContext,
            navigateToShowDetails: (showDetails: Long) -> Unit,
            navigateToSeason: (showTraktId: Long, seasonId: Long, seasonNumber: Long) -> Unit,
        ): WatchlistPresenter
    }
}
