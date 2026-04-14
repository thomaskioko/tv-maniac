package com.thomaskioko.tvmaniac.navigation.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.DefaultRootPresenter
import com.thomaskioko.tvmaniac.navigation.RootNavigator
import com.thomaskioko.tvmaniac.navigation.RootPresenter
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
@ContributesTo(ActivityScope::class)
public object NavigationBindingContainer {

    @Provides
    @SingleIn(ActivityScope::class)
    public fun provideRootPresenter(
        componentContext: ComponentContext,
        factory: DefaultRootPresenter.Factory,
        navigator: RootNavigator,
    ): RootPresenter = factory.create(componentContext, navigator)
}
