package com.thomaskioko.tvmaniac.navigation.navigators

import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.RootDestinationConfig
import com.thomaskioko.tvmaniac.navigation.RootNavigator
import com.thomaskioko.tvmaniac.profile.presenter.ProfileNavigator
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(ActivityScope::class)
public class DefaultProfileNavigator(
    private val rootNavigator: RootNavigator,
) : ProfileNavigator {
    override fun showSettings() {
        rootNavigator.pushNew(RootDestinationConfig.Settings)
    }
}
