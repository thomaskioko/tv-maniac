package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.popTo
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.pushToFront
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding

@Inject
@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class, binding = binding<RootNavigator>())
class DefaultRootNavigator : RootNavigator {
    private val navigation = StackNavigation<RootDestinationConfig>()

    override fun bringToFront(config: RootDestinationConfig) {
        navigation.bringToFront(config)
    }

    override fun pushNew(config: RootDestinationConfig) {
        navigation.pushNew(config)
    }

    override fun pushToFront(config: RootDestinationConfig) {
        navigation.pushToFront(config)
    }

    override fun pop() {
        navigation.pop()
    }

    override fun popTo(toIndex: Int) {
        navigation.popTo(index = toIndex)
    }

    override fun getStackNavigation(): StackNavigation<RootDestinationConfig> = navigation
}
