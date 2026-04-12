package com.thomaskioko.tvmaniac.navigation.navigators

import com.thomaskioko.nav.model.SeasonDetailsUiParam
import com.thomaskioko.nav.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.RootDestinationConfig
import com.thomaskioko.tvmaniac.navigation.RootNavigator
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsNavigator
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowSeasonDetailsParam
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(ActivityScope::class)
public class DefaultShowDetailsNavigator(
    private val rootNavigator: RootNavigator,
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
            RootDestinationConfig.SeasonDetails(
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
}
