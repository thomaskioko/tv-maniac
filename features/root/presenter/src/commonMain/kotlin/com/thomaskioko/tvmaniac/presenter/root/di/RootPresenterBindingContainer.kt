package com.thomaskioko.tvmaniac.presenter.root.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.RootNavigator
import com.thomaskioko.tvmaniac.navigation.root.ShowFollowedCallback
import com.thomaskioko.tvmaniac.presenter.root.DefaultRootPresenter
import com.thomaskioko.tvmaniac.presenter.root.RootPresenter
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
@ContributesTo(ActivityScope::class)
public object RootPresenterBindingContainer {

    @Provides
    @SingleIn(ActivityScope::class)
    public fun provideRootPresenter(
        componentContext: ComponentContext,
        factory: DefaultRootPresenter.Factory,
        navigator: RootNavigator,
    ): RootPresenter = factory.create(componentContext, navigator)

    @Provides
    @SingleIn(ActivityScope::class)
    public fun provideShowFollowedCallback(
        rootPresenter: RootPresenter,
    ): ShowFollowedCallback = rootPresenter as ShowFollowedCallback
}
