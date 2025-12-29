package com.thomaskioko.tvmaniac.testing.di.fakes

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.search.presenter.InitialSearchState
import com.thomaskioko.tvmaniac.search.presenter.SearchShowAction
import com.thomaskioko.tvmaniac.search.presenter.SearchShowState
import com.thomaskioko.tvmaniac.search.presenter.SearchShowsPresenter
import com.thomaskioko.tvmaniac.testing.di.TestScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(TestScope::class)
@ContributesBinding(TestScope::class, SearchShowsPresenter.Factory::class)
public class FakeSearchPresenterFactory : SearchShowsPresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        onNavigateToShowDetails: (id: Long) -> Unit,
        onNavigateToGenre: (id: Long) -> Unit,
    ): SearchShowsPresenter = FakeSearchShowsPresenter()
}

internal class FakeSearchShowsPresenter : SearchShowsPresenter {
    override val state: StateFlow<SearchShowState> = MutableStateFlow(InitialSearchState())

    override fun dispatch(action: SearchShowAction) {
        // No-op for testing
    }
}
