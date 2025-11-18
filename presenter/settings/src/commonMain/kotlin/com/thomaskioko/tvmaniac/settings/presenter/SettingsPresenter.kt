package com.thomaskioko.tvmaniac.settings.presenter

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow

interface SettingsPresenter {
    val state: StateFlow<SettingsState>
    fun dispatch(action: SettingsActions)

    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
        ): SettingsPresenter
    }
}
