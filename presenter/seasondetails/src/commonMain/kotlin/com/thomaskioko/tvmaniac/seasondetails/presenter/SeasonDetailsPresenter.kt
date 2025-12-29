package com.thomaskioko.tvmaniac.seasondetails.presenter

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.seasondetails.presenter.model.SeasonDetailsUiParam
import kotlinx.coroutines.flow.StateFlow

public interface SeasonDetailsPresenter {
    public val state: StateFlow<SeasonDetailsModel>
    public fun dispatch(action: SeasonDetailsAction)

    public interface Factory {
        public operator fun invoke(
            componentContext: ComponentContext,
            param: SeasonDetailsUiParam,
            onBack: () -> Unit,
            onNavigateToEpisodeDetails: (id: Long) -> Unit,
        ): SeasonDetailsPresenter
    }
}
