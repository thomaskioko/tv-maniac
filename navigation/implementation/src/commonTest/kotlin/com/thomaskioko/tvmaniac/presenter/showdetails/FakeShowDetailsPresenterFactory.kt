package com.thomaskioko.tvmaniac.presenter.showdetails

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowSeasonDetailsParam
import kotlinx.coroutines.flow.StateFlow

class FakeShowDetailsPresenterFactory : ShowDetailsPresenter.Factory {

    override fun invoke(
        componentContext: ComponentContext,
        id: Long,
        onBack: () -> Unit,
        onNavigateToShow: (id: Long) -> Unit,
        onNavigateToSeason: (param: ShowSeasonDetailsParam) -> Unit,
        onNavigateToTrailer: (id: Long) -> Unit,
    ): ShowDetailsPresenter = FakeShowDetailsPresenter()
}

internal class FakeShowDetailsPresenter : ShowDetailsPresenter {
    override val state: StateFlow<ShowDetailsContent>
        get() = TODO("Not yet implemented")

    override fun dispatch(action: ShowDetailsAction) {
        TODO("Not yet implemented")
    }
}
