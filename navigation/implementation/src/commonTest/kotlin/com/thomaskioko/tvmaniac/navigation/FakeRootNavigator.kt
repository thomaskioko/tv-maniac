package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.popTo
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.pushToFront

internal class FakeRootNavigator : RootNavigator {
    private val navigation = StackNavigation<NavRoute>()

    override fun bringToFront(route: NavRoute) {
        navigation.bringToFront(route)
    }

    override fun pushNew(route: NavRoute) {
        navigation.pushNew(route)
    }

    override fun pushToFront(route: NavRoute) {
        navigation.pushToFront(route)
    }

    override fun pop() {
        navigation.pop()
    }

    override fun popTo(toIndex: Int) {
        navigation.popTo(index = toIndex)
    }

    override fun getStackNavigation(): StackNavigation<NavRoute> = navigation
}
