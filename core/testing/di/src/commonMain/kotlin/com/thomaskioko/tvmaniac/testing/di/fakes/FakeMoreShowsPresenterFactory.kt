package com.thomaskioko.tvmaniac.testing.di.fakes

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.moreshows.presentation.MoreShowsActions
import com.thomaskioko.tvmaniac.moreshows.presentation.MoreShowsPresenter
import com.thomaskioko.tvmaniac.moreshows.presentation.MoreShowsState
import com.thomaskioko.tvmaniac.testing.di.TestScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(TestScope::class)
@ContributesBinding(TestScope::class, MoreShowsPresenter.Factory::class)
public class FakeMoreShowsPresenterFactory : MoreShowsPresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        id: Long,
        onBack: () -> Unit,
        onNavigateToShowDetails: (id: Long) -> Unit,
    ): MoreShowsPresenter = FakeMoreShowsPresenter()
}

internal class FakeMoreShowsPresenter : MoreShowsPresenter {
    override val state: StateFlow<MoreShowsState> = MutableStateFlow(MoreShowsState())
    override fun dispatch(action: MoreShowsActions) {
    }

    override fun onItemVisible(index: Int) {}
    override fun loadMore() {}
}
