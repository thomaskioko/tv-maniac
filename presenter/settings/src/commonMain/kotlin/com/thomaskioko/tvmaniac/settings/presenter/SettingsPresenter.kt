package com.thomaskioko.tvmaniac.settings.presenter

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow

public interface SettingsPresenter {
    public val state: StateFlow<SettingsState>
    public fun dispatch(action: SettingsActions)

    public interface Factory {
        public operator fun invoke(
            componentContext: ComponentContext,
            backClicked: () -> Unit,
        ): SettingsPresenter
    }
}
