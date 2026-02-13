package com.thomaskioko.tvmaniac.testing.di.fakes

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.debug.presenter.DebugActions
import com.thomaskioko.tvmaniac.debug.presenter.DebugPresenter
import com.thomaskioko.tvmaniac.debug.presenter.DebugState
import com.thomaskioko.tvmaniac.testing.di.TestScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(TestScope::class)
@ContributesBinding(TestScope::class, DebugPresenter.Factory::class)
public class FakeDebugPresenterFactory : DebugPresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        backClicked: () -> Unit,
    ): DebugPresenter = FakeDebugPresenter()
}

internal class FakeDebugPresenter : DebugPresenter {
    override val state: StateFlow<DebugState> = MutableStateFlow(DebugState.DEFAULT_STATE)

    override fun dispatch(action: DebugActions) {
    }
}
