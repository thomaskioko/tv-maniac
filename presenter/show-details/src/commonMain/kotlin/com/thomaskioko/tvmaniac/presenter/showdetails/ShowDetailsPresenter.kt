package com.thomaskioko.tvmaniac.presenter.showdetails

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowSeasonDetailsParam
import kotlinx.coroutines.flow.StateFlow

interface ShowDetailsPresenter {

    val state: StateFlow<ShowDetailsContent>
    fun dispatch(action: ShowDetailsAction)

    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            id: Long,
            onBack: () -> Unit,
            onNavigateToShow: (id: Long) -> Unit,
            onNavigateToSeason: (param: ShowSeasonDetailsParam) -> Unit,
            onNavigateToTrailer: (id: Long) -> Unit,
        ): ShowDetailsPresenter
    }
}
