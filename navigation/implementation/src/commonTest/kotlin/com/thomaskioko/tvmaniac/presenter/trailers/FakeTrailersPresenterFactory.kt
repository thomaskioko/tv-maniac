package com.thomaskioko.tvmaniac.presenter.trailers

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.trailers.testing.FakeTrailerRepository

class FakeTrailersPresenterFactory : TrailersPresenter.Factory {
    private val repository = FakeTrailerRepository()

    override fun invoke(
        componentContext: ComponentContext,
        traktShowId: Long,
    ): TrailersPresenter = TrailersPresenter(
        componentContext = componentContext,
        trailerId = traktShowId,
        repository = repository,
    )
}
