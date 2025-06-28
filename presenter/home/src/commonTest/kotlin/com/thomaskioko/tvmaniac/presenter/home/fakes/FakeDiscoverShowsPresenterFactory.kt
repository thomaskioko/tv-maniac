package com.thomaskioko.tvmaniac.presenter.home.fakes

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.discover.presenter.DefaultDiscoverShowsPresenter
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverShowAction
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverShowsPresenter
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverViewState
import kotlinx.coroutines.flow.StateFlow

class FakeDiscoverShowsPresenterFactory : DiscoverShowsPresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        onNavigateToShowDetails: (Long) -> Unit,
        onNavigateToMore: (Long) -> Unit,
    ): DiscoverShowsPresenter = FakeDiscoverShowsPresenter()
}

internal class FakeDiscoverShowsPresenter : DiscoverShowsPresenter {
    override val state: StateFlow<DiscoverViewState>
        get() = TODO("Not yet implemented")
    override val presenterInstance: DefaultDiscoverShowsPresenter.PresenterInstance
        get() = TODO("Not yet implemented")

    override fun dispatch(action: DiscoverShowAction) {
    }
}
