package com.thomaskioko.tvmaniac.testing.di.fakes

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.moreshows.presentation.MoreShowsPresenter
import com.thomaskioko.tvmaniac.navigation.DefaultRootPresenter
import com.thomaskioko.tvmaniac.navigation.RootNavigator
import com.thomaskioko.tvmaniac.navigation.RootPresenter
import com.thomaskioko.tvmaniac.presenter.home.HomePresenter
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsPresenter
import com.thomaskioko.tvmaniac.presenter.trailers.TrailersPresenter
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonDetailsPresenter
import com.thomaskioko.tvmaniac.testing.di.TestScope
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(TestScope::class)
@ContributesBinding(TestScope::class, RootPresenter.Factory::class)
class FakeRootPresenterFactory(
    private val homePresenterFactory: HomePresenter.Factory,
    private val moreShowsPresenterFactory: MoreShowsPresenter.Factory,
    private val showDetailsPresenterFactory: ShowDetailsPresenter.Factory,
    private val seasonDetailsPresenterFactory: SeasonDetailsPresenter.Factory,
    private val trailersPresenterFactory: TrailersPresenter.Factory,
    private val datastoreRepository: DatastoreRepository,
) : RootPresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        navigator: RootNavigator,
    ): RootPresenter {
        // Use the actual DefaultRootPresenter.Factory with injected dependencies
        val factory = DefaultRootPresenter.Factory(
            homePresenterFactory = homePresenterFactory,
            moreShowsPresenterFactory = moreShowsPresenterFactory,
            showDetailsPresenterFactory = showDetailsPresenterFactory,
            seasonDetailsPresenterFactory = seasonDetailsPresenterFactory,
            trailersPresenterFactory = trailersPresenterFactory,
            datastoreRepository = datastoreRepository,
        )
        return factory(componentContext, navigator)
    }
}
