package com.thomaskioko.tvmaniac.moreshows.presentation

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow

interface MoreShowsPresenter {

    val state: StateFlow<MoreShowsState>

    fun dispatch(action: MoreShowsActions)

    interface Factory {
        fun create(
            componentContext: ComponentContext,
            id: Long,
            onBack: () -> Unit,
            onNavigateToShowDetails: (id: Long) -> Unit,
        ): MoreShowsPresenter
    }
}
