package com.thomaskioko.tvmaniac.watchlist.presenter

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow

interface WatchlistPresenter {
    val state: StateFlow<WatchlistState>
    fun dispatch(action: WatchlistAction)

    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            navigateToShowDetails: (showDetails: Long) -> Unit,
            navigateToSeason: (showId: Long, seasonId: Long, seasonNumber: Long) -> Unit,
        ): WatchlistPresenter
    }
}
