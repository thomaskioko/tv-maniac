package com.thomaskioko.tvmaniac.presenter.trailers

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.flow.StateFlow

public interface TrailersPresenter {
    public val state: StateFlow<TrailersState>
    public val stateValue: Value<TrailersState>
    public fun dispatch(action: TrailersAction)

    public interface Factory {
        public operator fun invoke(
            componentContext: ComponentContext,
            traktShowId: Long,
        ): TrailersPresenter
    }
}
