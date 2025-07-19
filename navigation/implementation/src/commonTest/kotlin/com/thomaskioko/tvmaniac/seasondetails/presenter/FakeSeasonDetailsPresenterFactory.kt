package com.thomaskioko.tvmaniac.seasondetails.presenter

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.seasondetails.presenter.model.SeasonDetailsUiParam
import kotlinx.coroutines.flow.StateFlow

class FakeSeasonDetailsPresenterFactory : SeasonDetailsPresenter.Factory {

    override fun create(
        componentContext: ComponentContext,
        param: SeasonDetailsUiParam,
        onBack: () -> Unit,
        onNavigateToEpisodeDetails: (id: Long) -> Unit,
    ): SeasonDetailsPresenter = FakeSeasonDetailsPresenter()
}

internal class FakeSeasonDetailsPresenter : SeasonDetailsPresenter {
    override val state: StateFlow<SeasonDetailsModel>
        get() = TODO("Not yet implemented")

    override fun dispatch(action: SeasonDetailsAction) {
        TODO("Not yet implemented")
    }
}
