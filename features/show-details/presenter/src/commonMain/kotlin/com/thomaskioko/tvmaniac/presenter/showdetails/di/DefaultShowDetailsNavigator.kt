package com.thomaskioko.tvmaniac.presenter.showdetails.di

import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.RootNavigator
import com.thomaskioko.tvmaniac.navigation.model.RootDestinationConfig
import com.thomaskioko.tvmaniac.navigation.model.RootDestinationConfig.SeasonDetails
import com.thomaskioko.tvmaniac.navigation.root.ShowFollowedCallback
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsNavigator
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowSeasonDetailsParam
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(ActivityScope::class)
public class DefaultShowDetailsNavigator(
    private val rootNavigator: RootNavigator,
    private val showFollowedCallback: ShowFollowedCallback,
) : ShowDetailsNavigator {
    override fun goBack() {
        rootNavigator.pop()
    }

    override fun showDetails(traktId: Long) {
        rootNavigator.pushToFront(
            RootDestinationConfig.ShowDetails(param = ShowDetailsParam(id = traktId)),
        )
    }

    override fun showSeasonDetails(param: ShowSeasonDetailsParam) {
        rootNavigator.pushNew(
            SeasonDetails(
                param = SeasonDetailsUiParam(
                    showTraktId = param.showTraktId,
                    seasonNumber = param.seasonNumber,
                    seasonId = param.seasonId,
                ),
            ),
        )
    }

    override fun showTrailers(traktShowId: Long) {
        rootNavigator.pushNew(RootDestinationConfig.Trailers(traktShowId))
    }

    override fun showFollowed() {
        showFollowedCallback.onShowFollowed()
    }
}
