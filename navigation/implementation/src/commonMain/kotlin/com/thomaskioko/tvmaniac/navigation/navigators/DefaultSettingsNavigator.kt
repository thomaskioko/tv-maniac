package com.thomaskioko.tvmaniac.navigation.navigators

import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.RootDestinationConfig
import com.thomaskioko.tvmaniac.navigation.RootNavigator
import com.thomaskioko.tvmaniac.settings.presenter.SettingsNavigator
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(ActivityScope::class)
public class DefaultSettingsNavigator(
    private val rootNavigator: RootNavigator,
) : SettingsNavigator {
    override fun goBack() {
        rootNavigator.pop()
    }

    override fun showDebugMenu() {
        rootNavigator.pushNew(RootDestinationConfig.Debug)
    }
}
