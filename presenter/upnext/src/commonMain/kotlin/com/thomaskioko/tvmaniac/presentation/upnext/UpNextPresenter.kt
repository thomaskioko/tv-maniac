package com.thomaskioko.tvmaniac.presentation.upnext

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow

public interface UpNextPresenter {
    public val state: StateFlow<UpNextState>
    public fun dispatch(action: UpNextAction)

    public interface Factory {
        public operator fun invoke(
            componentContext: ComponentContext,
            navigateToShowDetails: (showTraktId: Long) -> Unit,
        ): UpNextPresenter
    }
}
