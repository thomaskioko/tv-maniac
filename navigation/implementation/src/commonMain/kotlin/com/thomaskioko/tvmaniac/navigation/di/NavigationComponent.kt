package com.thomaskioko.tvmaniac.navigation.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.navigation.RootNavigator
import com.thomaskioko.tvmaniac.navigation.RootPresenter
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(ActivityScope::class)
public interface NavigationComponent {

    @Provides
    @SingleIn(ActivityScope::class)
    public fun provideRootPresenter(
        componentContext: ComponentContext,
        factory: RootPresenter.Factory,
        navigator: RootNavigator,
    ): RootPresenter = factory(componentContext, navigator)
}
