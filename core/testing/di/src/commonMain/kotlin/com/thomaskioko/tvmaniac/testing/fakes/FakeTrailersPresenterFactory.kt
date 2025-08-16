package com.thomaskioko.tvmaniac.testing.fakes

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import com.thomaskioko.tvmaniac.presenter.trailers.TrailersPresenter
import com.thomaskioko.tvmaniac.testing.TestScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(TestScope::class)
@ContributesBinding(TestScope::class)
class FakeTrailersPresenterFactory(
    private val repository: TrailerRepository,
) : TrailersPresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        traktShowId: Long,
    ): TrailersPresenter = TrailersPresenter(
        componentContext = componentContext,
        trailerId = traktShowId,
        repository = repository,
    )
}
