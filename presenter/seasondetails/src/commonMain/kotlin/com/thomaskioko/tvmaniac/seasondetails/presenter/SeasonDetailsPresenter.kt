package com.thomaskioko.tvmaniac.seasondetails.presenter

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.seasondetails.presenter.model.SeasonDetailsUiParam
import kotlinx.coroutines.flow.StateFlow

interface SeasonDetailsPresenter {
    val state: StateFlow<SeasonDetailsModel>
    fun dispatch(action: SeasonDetailsAction)

    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            param: SeasonDetailsUiParam,
            onBack: () -> Unit,
            onNavigateToEpisodeDetails: (id: Long) -> Unit,
        ): SeasonDetailsPresenter
    }
}
