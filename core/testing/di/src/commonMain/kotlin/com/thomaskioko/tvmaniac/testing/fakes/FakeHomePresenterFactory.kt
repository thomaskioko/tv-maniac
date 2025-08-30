package com.thomaskioko.tvmaniac.testing.fakes

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverShowsPresenter
import com.thomaskioko.tvmaniac.presenter.home.DefaultHomePresenter
import com.thomaskioko.tvmaniac.search.presenter.SearchShowsPresenter
import com.thomaskioko.tvmaniac.settings.presenter.SettingsPresenter
import com.thomaskioko.tvmaniac.testing.TestScope
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistPresenter
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(TestScope::class)
@ContributesBinding(TestScope::class)
class FakeHomePresenterFactory(
    private val discoverPresenterFactory: DiscoverShowsPresenter.Factory,
    private val watchlistPresenterFactory: WatchlistPresenter.Factory,
    private val searchPresenterFactory: SearchShowsPresenter.Factory,
    private val settingsPresenterFactory: SettingsPresenter.Factory,
    private val traktAuthManager: TraktAuthManager,
) : DefaultHomePresenter.Factory {
    override fun create(
        componentContext: ComponentContext,
        onShowClicked: (id: Long) -> Unit,
        onMoreShowClicked: (id: Long) -> Unit,
        onShowGenreClicked: (id: Long) -> Unit,
    ): DefaultHomePresenter {
        return DefaultHomePresenter(
            discoverPresenterFactory = discoverPresenterFactory,
            watchlistPresenterFactory = watchlistPresenterFactory,
            searchPresenterFactory = searchPresenterFactory,
            settingsPresenterFactory = settingsPresenterFactory,
            traktAuthManager = traktAuthManager,
            componentContext = componentContext,
            onShowClicked = onShowClicked,
            onMoreShowClicked = onShowClicked,
            onShowGenreClicked = onShowClicked,
        )
    }
}
