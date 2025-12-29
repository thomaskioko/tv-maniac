package com.thomaskioko.tvmaniac.search.presenter

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow

public interface SearchShowsPresenter {

    public val state: StateFlow<SearchShowState>
    public fun dispatch(action: SearchShowAction)

    public interface Factory {
        public operator fun invoke(
            componentContext: ComponentContext,
            onNavigateToShowDetails: (id: Long) -> Unit,
            onNavigateToGenre: (id: Long) -> Unit,
        ): SearchShowsPresenter
    }
}
