package com.thomaskioko.tvmaniac.settings.presenter.di

import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.debug.nav.DebugRoute
import com.thomaskioko.tvmaniac.navigation.RootNavigator
import com.thomaskioko.tvmaniac.settings.nav.SettingsNavigator
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(ActivityScope::class)
public class DefaultSettingsNavigator(
    private val rootNavigator: RootNavigator,
) : SettingsNavigator {
    override fun goBack() {
        rootNavigator.pop()
    }

    override fun showDebugMenu() {
        rootNavigator.pushNew(DebugRoute)
    }
}
