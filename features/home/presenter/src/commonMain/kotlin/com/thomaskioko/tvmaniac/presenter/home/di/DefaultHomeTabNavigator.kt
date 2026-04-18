package com.thomaskioko.tvmaniac.presenter.home.di

import com.arkivanov.decompose.router.stack.StackNavigation
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.home.nav.HomeTabNavigator
import com.thomaskioko.tvmaniac.home.nav.di.model.HomeConfig
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class)
public class DefaultHomeTabNavigator : HomeTabNavigator {
    private var navigation: StackNavigation<HomeConfig>? = null

    override fun registerNavigation(navigation: StackNavigation<HomeConfig>) {
        this.navigation = navigation
    }

    override fun unregisterNavigation() {
        this.navigation = null
    }

    override fun switchToProgressTab() {
        navigation?.navigate(
            transformer = { stack ->
                val existing = stack.find { it is HomeConfig.Progress }
                if (existing != null) {
                    stack.filterNot { it is HomeConfig.Progress } + existing
                } else {
                    stack + HomeConfig.Progress
                }
            },
            onComplete = { _, _ -> },
        )
    }
}
