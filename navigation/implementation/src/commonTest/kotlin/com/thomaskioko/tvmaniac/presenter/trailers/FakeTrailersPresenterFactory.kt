package com.thomaskioko.tvmaniac.presenter.trailers

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.presenter.trailers.di.TrailersPresenterFactory
import com.thomaskioko.tvmaniac.trailers.testing.FakeTrailerRepository

/**
 * A fake implementation of [TrailersPresenterFactory] for testing.
 * This simplifies the creation of [TrailersPresenter] in tests by handling all the dependencies internally.
 */
class FakeTrailersPresenterFactory : TrailersPresenterFactory {
    private val repository = FakeTrailerRepository()

    override fun create(
        componentContext: ComponentContext,
        id: Long,
    ): TrailersPresenter = TrailersPresenter(
        componentContext = componentContext,
        traktShowId = id,
        repository = repository,
    )
}
