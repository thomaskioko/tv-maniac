package com.thomaskioko.tvmaniac.testing.di.fakes

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.presentation.upnext.UpNextAction
import com.thomaskioko.tvmaniac.presentation.upnext.UpNextPresenter
import com.thomaskioko.tvmaniac.presentation.upnext.UpNextState
import com.thomaskioko.tvmaniac.testing.di.TestScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(TestScope::class)
@ContributesBinding(TestScope::class, UpNextPresenter.Factory::class)
public class FakeUpNextPresenterFactory : UpNextPresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        navigateToShowDetails: (showTraktId: Long) -> Unit,
    ): UpNextPresenter = FakeUpNextPresenter()
}

internal class FakeUpNextPresenter : UpNextPresenter {
    override val state: StateFlow<UpNextState> = MutableStateFlow(UpNextState())

    override fun dispatch(action: UpNextAction) {
    }
}
