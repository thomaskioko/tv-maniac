package com.thomaskioko.tvmaniac.search.presenter

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow

class FakeSearchPresenterFactory : SearchShowsPresenter.Factory {

    override fun invoke(
        componentContext: ComponentContext,
        onNavigateToShowDetails: (id: Long) -> Unit,
        onNavigateToGenre: (id: Long) -> Unit,
    ): SearchShowsPresenter = FakeSearchShowsPresenter()
}

internal class FakeSearchShowsPresenter : SearchShowsPresenter {
    override val state: StateFlow<SearchShowState>
        get() = TODO("Not yet implemented")

    override fun dispatch(action: SearchShowAction) {
        TODO("Not yet implemented")
    }
}
