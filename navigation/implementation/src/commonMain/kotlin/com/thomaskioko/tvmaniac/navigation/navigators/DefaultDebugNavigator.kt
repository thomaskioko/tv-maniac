package com.thomaskioko.tvmaniac.navigation.navigators

import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.debug.presenter.DebugNavigator
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
