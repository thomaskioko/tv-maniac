package com.thomaskioko.tvmaniac.navigation.navigators

import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.RootNavigator
import com.thomaskioko.tvmaniac.navigation.model.RootDestinationConfig
import com.thomaskioko.tvmaniac.navigation.model.RootDestinationConfig.SeasonDetails
import com.thomaskioko.tvmaniac.presenter.root.RootPresenter
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsNavigator
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsParam
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowSeasonDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonDetailsUiParam
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(ActivityScope::class)
public class DefaultShowDetailsNavigator(
    private val rootNavigator: RootNavigator,
    private val rootPresenter: RootPresenter,
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
        rootPresenter.onShowFollowed()
    }
}
