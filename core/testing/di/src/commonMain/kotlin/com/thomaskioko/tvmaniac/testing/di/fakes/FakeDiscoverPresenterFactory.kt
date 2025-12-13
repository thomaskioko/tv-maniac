package com.thomaskioko.tvmaniac.testing.di.fakes

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.discover.presenter.DefaultDiscoverShowsPresenter
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverShowAction
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverShowsPresenter
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverViewState
import com.thomaskioko.tvmaniac.testing.di.TestScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(TestScope::class)
@ContributesBinding(TestScope::class, DiscoverShowsPresenter.Factory::class)
class FakeDiscoverPresenterFactory : DiscoverShowsPresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        onNavigateToShowDetails: (id: Long) -> Unit,
        onNavigateToMore: (categoryId: Long) -> Unit,
        onNavigateToEpisode: (showId: Long, episodeId: Long) -> Unit,
        onNavigateToSeason: (showId: Long, seasonId: Long, seasonNumber: Long) -> Unit,
    ): DiscoverShowsPresenter = FakeDiscoverShowsPresenter()
}

internal class FakeDiscoverShowsPresenter : DiscoverShowsPresenter {
    override val state: StateFlow<DiscoverViewState> = MutableStateFlow(DiscoverViewState())

    override val presenterInstance: DefaultDiscoverShowsPresenter.PresenterInstance
        get() = throw UnsupportedOperationException("FakeDiscoverShowsPresenter does not support presenterInstance")

    override fun dispatch(action: DiscoverShowAction) {
        // No-op for testing
    }
}
