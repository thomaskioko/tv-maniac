package com.thomaskioko.tvmaniac.presenter.trailers

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow

public interface TrailersPresenter {
    public val state: StateFlow<TrailersState>
    public fun dispatch(action: TrailersAction)

    public interface Factory {
        public operator fun invoke(
            componentContext: ComponentContext,
            traktShowId: Long,
        ): TrailersPresenter
    }
}
