package com.thomaskioko.tvmaniac.navigation.navigators

import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.RootDestinationConfig
import com.thomaskioko.tvmaniac.navigation.RootNavigator
import com.thomaskioko.tvmaniac.navigation.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.search.presenter.SearchNavigator
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(ActivityScope::class)
public class DefaultSearchNavigator(
    private val rootNavigator: RootNavigator,
) : SearchNavigator {
    override fun showDetails(traktId: Long) {
        rootNavigator.pushNew(
            RootDestinationConfig.ShowDetails(param = ShowDetailsParam(id = traktId)),
        )
    }

    override fun showGenre(genreId: Long) {
        rootNavigator.pushNew(RootDestinationConfig.GenreShows(genreId))
    }

    override fun goBack() {
        rootNavigator.pop()
    }
}
