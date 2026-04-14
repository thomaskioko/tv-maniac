package com.thomaskioko.tvmaniac.navigation.navigators

import com.thomaskioko.nav.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.RootNavigator
import com.thomaskioko.tvmaniac.navigation.model.RootDestinationConfig
import com.thomaskioko.tvmaniac.presentation.library.LibraryNavigator
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(ActivityScope::class)
public class DefaultLibraryNavigator(
    private val rootNavigator: RootNavigator,
) : LibraryNavigator {
    override fun showDetails(traktId: Long) {
        rootNavigator.pushNew(
            RootDestinationConfig.ShowDetails(param = ShowDetailsParam(id = traktId)),
        )
    }
}
