package com.thomaskioko.tvmaniac.navigation.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.navigation.DefaultRootPresenter
import com.thomaskioko.tvmaniac.navigation.RootNavigator
import com.thomaskioko.tvmaniac.navigation.RootPresenter
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@ContributesTo(ActivityScope::class)
interface NavigationComponent {

    @Provides
    @SingleIn(ActivityScope::class)
    fun provideRootPresenter(
        componentContext: ComponentContext,
        factory: DefaultRootPresenter.Factory,
        navigator: RootNavigator,
    ): RootPresenter = factory.create(componentContext, navigator)
}
