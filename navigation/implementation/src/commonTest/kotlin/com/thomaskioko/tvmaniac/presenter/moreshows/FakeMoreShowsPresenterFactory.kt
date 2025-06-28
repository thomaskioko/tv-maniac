package com.thomaskioko.tvmaniac.presenter.moreshows

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.moreshows.presentation.MoreShowsActions
import com.thomaskioko.tvmaniac.moreshows.presentation.MoreShowsPresenter
import com.thomaskioko.tvmaniac.moreshows.presentation.MoreShowsState
import kotlinx.coroutines.flow.StateFlow

class FakeMoreShowsPresenterFactory : MoreShowsPresenter.Factory {
    override fun create(
        componentContext: ComponentContext,
        id: Long,
        onBack: () -> Unit,
        onNavigateToShowDetails: (id: Long) -> Unit,
    ): MoreShowsPresenter = FakeMoreShowsPresenter()
}

internal class FakeMoreShowsPresenter : MoreShowsPresenter {
    override val state: StateFlow<MoreShowsState>
        get() = TODO("Not yet implemented")

    override fun dispatch(action: MoreShowsActions) {
    }
}
