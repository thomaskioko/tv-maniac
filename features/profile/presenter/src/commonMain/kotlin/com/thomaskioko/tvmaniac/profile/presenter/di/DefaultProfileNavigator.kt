package com.thomaskioko.tvmaniac.profile.presenter.di

import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.RootNavigator
import com.thomaskioko.tvmaniac.navigation.model.RootDestinationConfig
import com.thomaskioko.tvmaniac.profile.nav.ProfileNavigator
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(ActivityScope::class)
public class DefaultProfileNavigator(
    private val rootNavigator: RootNavigator,
) : ProfileNavigator {
    override fun showSettings() {
        rootNavigator.pushNew(RootDestinationConfig.Settings)
    }
}
