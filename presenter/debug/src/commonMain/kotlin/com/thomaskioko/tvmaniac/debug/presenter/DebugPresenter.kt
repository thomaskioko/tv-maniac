package com.thomaskioko.tvmaniac.debug.presenter

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.flow.StateFlow

public interface DebugPresenter {
    public val state: StateFlow<DebugState>
    public val stateValue: Value<DebugState>
    public fun dispatch(action: DebugActions)

    public interface Factory {
        public operator fun invoke(
            componentContext: ComponentContext,
            backClicked: () -> Unit,
        ): DebugPresenter
    }
}
