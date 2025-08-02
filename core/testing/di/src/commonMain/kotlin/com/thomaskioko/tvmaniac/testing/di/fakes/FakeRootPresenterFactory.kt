package com.thomaskioko.tvmaniac.testing.di.fakes

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.navigation.DefaultRootPresenter
import com.thomaskioko.tvmaniac.navigation.RootNavigator
import com.thomaskioko.tvmaniac.navigation.RootPresenter

class FakeRootPresenterFactory : RootPresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        navigator: RootNavigator,
    ): RootPresenter {
        // Create the actual factory and use it
        val factory = DefaultRootPresenter.Factory(
            homePresenterFactory = FakeHomePresenterFactory(),
            moreShowsPresenterFactory = FakeMoreShowsPresenterFactory(),
            showDetailsPresenterFactory = FakeShowDetailsPresenterFactory(),
            seasonDetailsPresenterFactory = FakeSeasonDetailsPresenterFactory(),
            trailersPresenterFactory = FakeTrailersPresenterFactory(),
            datastoreRepository = FakeDatastoreRepository(),
        )
        return factory(componentContext, navigator)
    }
}
