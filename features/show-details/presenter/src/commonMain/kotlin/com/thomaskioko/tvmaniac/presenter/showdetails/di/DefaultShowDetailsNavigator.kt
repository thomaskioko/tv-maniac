package com.thomaskioko.tvmaniac.presenter.showdetails.di

import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.RootNavigator
import com.thomaskioko.tvmaniac.navigation.root.ShowFollowedCallback
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsRoute
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsNavigator
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowSeasonDetailsParam
import com.thomaskioko.tvmaniac.trailers.nav.TrailersRoute
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
        rootNavigator.pushToFront(ShowDetailsRoute(param = ShowDetailsParam(id = traktId)))
    }

    override fun showSeasonDetails(param: ShowSeasonDetailsParam) {
        rootNavigator.pushNew(
            SeasonDetailsRoute(
                param = SeasonDetailsUiParam(
                    showTraktId = param.showTraktId,
                    seasonNumber = param.seasonNumber,
                    seasonId = param.seasonId,
                ),
            ),
        )
    }

    override fun showTrailers(traktShowId: Long) {
        rootNavigator.pushNew(TrailersRoute(traktShowId))
    }

    override fun showFollowed() {
        showFollowedCallback.onShowFollowed()
    }
}
