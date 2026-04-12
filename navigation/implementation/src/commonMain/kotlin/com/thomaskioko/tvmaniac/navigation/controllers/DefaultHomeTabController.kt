package com.thomaskioko.tvmaniac.navigation.controllers

import com.arkivanov.decompose.router.stack.StackNavigation
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.presenter.home.HomePresenter.HomeConfig
import com.thomaskioko.tvmaniac.presenter.home.HomeTabController
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class)
public class DefaultHomeTabController : HomeTabController {
    private var navigation: StackNavigation<HomeConfig>? = null

    public fun register(navigation: StackNavigation<HomeConfig>) {
        this.navigation = navigation
    }

    public fun unregister() {
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
