package com.thomaskioko.tvmaniac.settings.presenter

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow

class FakeSettingsPresenterFactory : SettingsPresenter.Factory {

    override fun create(
        componentContext: ComponentContext,
        launchWebView: () -> Unit,
    ): SettingsPresenter = FakeSettingsPresenter()
}

internal class FakeSettingsPresenter : SettingsPresenter {
    override val state: StateFlow<SettingsState>
        get() = TODO("Not yet implemented")

    override fun dispatch(action: SettingsActions) {
    }
}
