package com.thomaskioko.tvmaniac.testing.di.fakes

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverShowsPresenter
import com.thomaskioko.tvmaniac.presentation.library.LibraryPresenter
import com.thomaskioko.tvmaniac.presentation.upnext.UpNextPresenter
import com.thomaskioko.tvmaniac.presenter.home.DefaultHomePresenter
import com.thomaskioko.tvmaniac.presenter.home.HomePresenter
import com.thomaskioko.tvmaniac.search.presenter.SearchShowsPresenter
import com.thomaskioko.tvmaniac.testing.di.TestScope
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(TestScope::class)
@ContributesBinding(TestScope::class, HomePresenter.Factory::class)
public class FakeHomePresenterFactory(
    private val discoverPresenterFactory: DiscoverShowsPresenter.Factory,
    private val libraryPresenterFactory: LibraryPresenter.Factory,
    private val searchPresenterFactory: SearchShowsPresenter.Factory,
    private val upNextPresenterFactory: UpNextPresenter.Factory,
) : HomePresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        onShowClicked: (id: Long) -> Unit,
        onMoreShowClicked: (id: Long) -> Unit,
        onShowGenreClicked: (id: Long) -> Unit,
        onNavigateToProfile: () -> Unit,
        onSettingsClicked: () -> Unit,
    ): HomePresenter {
        val factory = DefaultHomePresenter.Factory(
            discoverPresenterFactory = discoverPresenterFactory,
            libraryPresenterFactory = libraryPresenterFactory,
            searchPresenterFactory = searchPresenterFactory,
            upNextPresenterFactory = upNextPresenterFactory,
        )
        return factory(componentContext, onShowClicked, onMoreShowClicked, onShowGenreClicked, onNavigateToProfile, onSettingsClicked)
    }
}
