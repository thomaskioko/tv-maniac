package com.thomaskioko.tvmaniac.presenter.trailers

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow

interface TrailersPresenter {
    val state: StateFlow<TrailersState>
    fun dispatch(action: TrailersAction)

    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            traktShowId: Long,
        ): TrailersPresenter
    }
}
