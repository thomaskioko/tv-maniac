package com.thomaskioko.tvmaniac.search.presenter.di

import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.genreshows.nav.GenreShowsRoute
import com.thomaskioko.tvmaniac.navigation.RootNavigator
import com.thomaskioko.tvmaniac.search.nav.SearchNavigator
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(ActivityScope::class)
public class DefaultSearchNavigator(
    private val rootNavigator: RootNavigator,
) : SearchNavigator {
    override fun showDetails(traktId: Long) {
        rootNavigator.pushNew(ShowDetailsRoute(param = ShowDetailsParam(id = traktId)))
    }

    override fun showGenre(genreId: Long) {
        rootNavigator.pushNew(GenreShowsRoute(genreId))
    }

    override fun goBack() {
        rootNavigator.pop()
    }
}
