package com.thomaskioko.tvmaniac.testing.di.fakes

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverShowsPresenter
import com.thomaskioko.tvmaniac.presenter.home.DefaultHomePresenter
import com.thomaskioko.tvmaniac.presenter.home.HomePresenter
import com.thomaskioko.tvmaniac.search.presenter.SearchShowsPresenter
import com.thomaskioko.tvmaniac.settings.presenter.SettingsPresenter
import com.thomaskioko.tvmaniac.testing.di.TestScope
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistPresenter
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(TestScope::class)
@ContributesBinding(TestScope::class, HomePresenter.Factory::class)
class FakeHomePresenterFactory(
    private val discoverPresenterFactory: DiscoverShowsPresenter.Factory,
    private val watchlistPresenterFactory: WatchlistPresenter.Factory,
    private val searchPresenterFactory: SearchShowsPresenter.Factory,
    private val settingsPresenterFactory: SettingsPresenter.Factory,
    private val traktAuthManager: TraktAuthManager,
) : HomePresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        onShowClicked: (id: Long) -> Unit,
        onMoreShowClicked: (id: Long) -> Unit,
        onShowGenreClicked: (id: Long) -> Unit,
    ): HomePresenter {
        // Use the actual DefaultHomePresenter.Factory with injected dependencies
        val factory = DefaultHomePresenter.Factory(
            discoverPresenterFactory = discoverPresenterFactory,
            watchlistPresenterFactory = watchlistPresenterFactory,
            searchPresenterFactory = searchPresenterFactory,
            settingsPresenterFactory = settingsPresenterFactory,
            traktAuthManager = traktAuthManager,
        )
        return factory(componentContext, onShowClicked, onMoreShowClicked, onShowGenreClicked)
    }
}
