package com.thomaskioko.tvmaniac.presentation.library

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow

public interface LibraryPresenter {
    public val state: StateFlow<LibraryState>
    public fun dispatch(action: LibraryAction)

    public interface Factory {
        public operator fun invoke(
            componentContext: ComponentContext,
            navigateToShowDetails: (showDetails: Long) -> Unit,
        ): LibraryPresenter
    }
}
