package com.thomaskioko.tvmaniac.navigation.navigators

import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.moreshows.presentation.MoreShowsNavigator
import com.thomaskioko.tvmaniac.navigation.RootNavigator
import com.thomaskioko.tvmaniac.navigation.model.RootDestinationConfig
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsParam
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(ActivityScope::class)
public class DefaultMoreShowsNavigator(
    private val rootNavigator: RootNavigator,
) : MoreShowsNavigator {
    override fun goBack() {
        rootNavigator.pop()
    }

    override fun showDetails(traktId: Long) {
        rootNavigator.pushNew(
            RootDestinationConfig.ShowDetails(param = ShowDetailsParam(id = traktId)),
        )
    }
}
