package com.thomaskioko.tvmaniac.testing.di.fakes

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.settings.presenter.SettingsActions
import com.thomaskioko.tvmaniac.settings.presenter.SettingsPresenter
import com.thomaskioko.tvmaniac.settings.presenter.SettingsState
import com.thomaskioko.tvmaniac.testing.di.TestScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(TestScope::class)
@ContributesBinding(TestScope::class, SettingsPresenter.Factory::class)
class FakeSettingsPresenterFactory : SettingsPresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        backClicked: () -> Unit,
    ): SettingsPresenter = FakeSettingsPresenter()
}

internal class FakeSettingsPresenter : SettingsPresenter {
    override val state: StateFlow<SettingsState> = MutableStateFlow(SettingsState.DEFAULT_STATE)

    override fun dispatch(action: SettingsActions) {
        // No-op for testing
    }
}
