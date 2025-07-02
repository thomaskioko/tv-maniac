package com.thomaskioko.tvmaniac.search.presenter

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow

interface SearchShowsPresenter {

    val state: StateFlow<SearchShowState>
    fun dispatch(action: SearchShowAction)

    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onNavigateToShowDetails: (id: Long) -> Unit,
            onNavigateToGenre: (id: Long) -> Unit,
        ): SearchShowsPresenter
    }
}
