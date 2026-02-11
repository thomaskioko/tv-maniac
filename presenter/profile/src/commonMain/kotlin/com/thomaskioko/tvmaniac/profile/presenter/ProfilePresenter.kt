package com.thomaskioko.tvmaniac.profile.presenter

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileState
import kotlinx.coroutines.flow.StateFlow

public interface ProfilePresenter {
    public val state: StateFlow<ProfileState>
    public fun dispatch(action: ProfileAction)

    public interface Factory {
        public operator fun invoke(
            componentContext: ComponentContext,
            navigateBack: () -> Unit,
            navigateToSettings: () -> Unit,
        ): ProfilePresenter
    }
}
