package com.thomaskioko.tvmaniac.presenter.showdetails

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowSeasonDetailsParam
import kotlinx.coroutines.flow.StateFlow

public interface ShowDetailsPresenter {

    public val state: StateFlow<ShowDetailsContent>
    public fun dispatch(action: ShowDetailsAction)

    public interface Factory {
        public operator fun invoke(
            componentContext: ComponentContext,
            id: Long,
            onBack: () -> Unit,
            onNavigateToShow: (id: Long) -> Unit,
            onNavigateToSeason: (param: ShowSeasonDetailsParam) -> Unit,
            onNavigateToTrailer: (id: Long) -> Unit,
        ): ShowDetailsPresenter
    }
}
