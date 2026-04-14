package com.thomaskioko.tvmaniac.debug.presenter.di

import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.debug.nav.DebugNavigator
import com.thomaskioko.tvmaniac.navigation.RootNavigator
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(ActivityScope::class)
public class DefaultDebugNavigator(
    private val rootNavigator: RootNavigator,
) : DebugNavigator {
    override fun goBack() {
        rootNavigator.pop()
    }
}
