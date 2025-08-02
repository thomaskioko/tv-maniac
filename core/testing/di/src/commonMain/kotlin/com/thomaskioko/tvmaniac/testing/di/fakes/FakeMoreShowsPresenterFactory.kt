package com.thomaskioko.tvmaniac.testing.di.fakes

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.moreshows.presentation.MoreShowsActions
import com.thomaskioko.tvmaniac.moreshows.presentation.MoreShowsPresenter
import com.thomaskioko.tvmaniac.moreshows.presentation.MoreShowsState
import com.thomaskioko.tvmaniac.moreshows.presentation.TvShow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeMoreShowsPresenterFactory : MoreShowsPresenter.Factory {
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

    override fun getElement(index: Int): TvShow? = null
}
