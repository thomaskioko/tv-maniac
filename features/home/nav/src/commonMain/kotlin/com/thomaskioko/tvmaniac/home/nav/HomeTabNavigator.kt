package com.thomaskioko.tvmaniac.home.nav

import com.arkivanov.decompose.router.stack.StackNavigation
import com.thomaskioko.tvmaniac.home.nav.di.model.HomeConfig

public interface HomeTabNavigator {
    public fun registerNavigation(navigation: StackNavigation<HomeConfig>)
    public fun unregisterNavigation()
    public fun switchToProgressTab()
}
