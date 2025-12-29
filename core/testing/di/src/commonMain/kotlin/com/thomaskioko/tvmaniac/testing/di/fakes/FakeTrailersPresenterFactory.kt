package com.thomaskioko.tvmaniac.testing.di.fakes

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.presenter.trailers.TrailersAction
import com.thomaskioko.tvmaniac.presenter.trailers.TrailersContent
import com.thomaskioko.tvmaniac.presenter.trailers.TrailersPresenter
import com.thomaskioko.tvmaniac.presenter.trailers.TrailersState
import com.thomaskioko.tvmaniac.testing.di.TestScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(TestScope::class)
@ContributesBinding(TestScope::class, TrailersPresenter.Factory::class)
public class FakeTrailersPresenterFactory : TrailersPresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        traktShowId: Long,
    ): TrailersPresenter = FakeTrailersPresenter()
}

internal class FakeTrailersPresenter : TrailersPresenter {
    override val state: StateFlow<TrailersState> = MutableStateFlow(TrailersContent())

    override fun dispatch(action: TrailersAction) {
        // No-op for testing
    }
}
