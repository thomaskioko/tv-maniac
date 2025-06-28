package com.thomaskioko.tvmaniac.presenter.home.fakes

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.settings.presenter.SettingsActions
import com.thomaskioko.tvmaniac.settings.presenter.SettingsPresenter
import com.thomaskioko.tvmaniac.settings.presenter.SettingsState
import kotlinx.coroutines.flow.StateFlow

class FakeSettingsPresenterFactory : SettingsPresenter.Factory {

    override fun invoke(
        componentContext: ComponentContext,
        launchWebView: () -> Unit,
    ): SettingsPresenter = FakeSettingsPresenter()
}

internal class FakeSettingsPresenter : SettingsPresenter {
    override val state: StateFlow<SettingsState>
        get() = TODO("Not yet implemented")

    override fun dispatch(action: SettingsActions) {
        TODO("Not yet implemented")
    }
}
