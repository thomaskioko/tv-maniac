package com.thomaskioko.tvmaniac.moreshows.presentation

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow

public interface MoreShowsPresenter {

    public val state: StateFlow<MoreShowsState>

    public fun dispatch(action: MoreShowsActions)

    public fun onItemVisible(index: Int)

    public interface Factory {
        public operator fun invoke(
            componentContext: ComponentContext,
            id: Long,
            onBack: () -> Unit,
            onNavigateToShowDetails: (id: Long) -> Unit,
        ): MoreShowsPresenter
    }
}
