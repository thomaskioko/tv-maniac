package com.thomaskioko.tvmaniac.shared

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.navigation.RootNavigator
import com.thomaskioko.tvmaniac.navigation.RootPresenter
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesSubcomponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesSubcomponent(ActivityScope::class)
@SingleIn(ActivityScope::class)
public interface IosViewPresenterComponent {
    public val rootPresenter: RootPresenter
    public val rootNavigator: RootNavigator

    @ContributesSubcomponent.Factory(AppScope::class)
    public interface Factory {
        public fun createComponent(
            componentContext: ComponentContext,
        ): IosViewPresenterComponent
    }
}
