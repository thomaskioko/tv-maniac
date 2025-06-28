package com.thomaskioko.tvmaniac.presenter.home.fakes

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.search.presenter.SearchShowAction
import com.thomaskioko.tvmaniac.search.presenter.SearchShowState
import com.thomaskioko.tvmaniac.search.presenter.SearchShowsPresenter
import kotlinx.coroutines.flow.StateFlow

class FakeSearchPresenterFactory : SearchShowsPresenter.Factory {

    override fun invoke(
        componentContext: ComponentContext,
        onNavigateToShowDetails: (id: Long) -> Unit,
        onNavigateToGenre: (id: Long) -> Unit,
    ): SearchShowsPresenter = FakeSearchPresenter()
}

internal class FakeSearchPresenter : SearchShowsPresenter {
    override val state: StateFlow<SearchShowState>
        get() = TODO("Not yet implemented")

    override fun dispatch(action: SearchShowAction) {
        TODO("Not yet implemented")
    }
}
