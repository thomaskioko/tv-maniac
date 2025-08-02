package com.thomaskioko.tvmaniac.testing.di.fakes

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.presenter.home.DefaultHomePresenter
import com.thomaskioko.tvmaniac.presenter.home.HomePresenter
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthManager

class FakeHomePresenterFactory : HomePresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        onShowClicked: (id: Long) -> Unit,
        onMoreShowClicked: (id: Long) -> Unit,
        onShowGenreClicked: (id: Long) -> Unit,
    ): HomePresenter {
        // Create the actual factory and use it
        val factory = DefaultHomePresenter.Factory(
            discoverPresenterFactory = FakeDiscoverPresenterFactory(),
            watchlistPresenterFactory = FakeWatchlistPresenterFactory(),
            searchPresenterFactory = FakeSearchPresenterFactory(),
            settingsPresenterFactory = FakeSettingsPresenterFactory(),
            traktAuthManager = FakeTraktAuthManager(),
        )
        return factory(componentContext, onShowClicked, onMoreShowClicked, onShowGenreClicked)
    }
}
