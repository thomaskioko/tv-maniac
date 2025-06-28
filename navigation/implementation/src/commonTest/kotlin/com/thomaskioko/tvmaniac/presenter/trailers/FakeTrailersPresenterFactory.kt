package com.thomaskioko.tvmaniac.presenter.trailers

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow

class FakeTrailersPresenterFactory : TrailersPresenter.Factory {

    override fun invoke(
        componentContext: ComponentContext,
        traktShowId: Long,
    ): TrailersPresenter = FakeTrailersPresenter()
}

internal class FakeTrailersPresenter : TrailersPresenter {
    override val state: StateFlow<TrailersState>
        get() = TODO("Not yet implemented")

    override fun dispatch(action: TrailersAction) {
        TODO("Not yet implemented")
    }
}
