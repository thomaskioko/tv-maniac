package com.thomaskioko.tvmaniac.testing.di.fakes

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.profile.presenter.ProfileAction
import com.thomaskioko.tvmaniac.profile.presenter.ProfilePresenter
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileState
import com.thomaskioko.tvmaniac.testing.di.TestScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(TestScope::class)
@ContributesBinding(TestScope::class, ProfilePresenter.Factory::class)
public class FakeProfilePresenterFactory : ProfilePresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        navigateToSettings: () -> Unit,
    ): ProfilePresenter = FakeProfilePresenter()
}

internal class FakeProfilePresenter : ProfilePresenter {
    override val state: StateFlow<ProfileState> = MutableStateFlow(ProfileState.DEFAULT_STATE)

    override fun dispatch(action: ProfileAction) {
        // No-op for testing
    }
}
