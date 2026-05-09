package com.thomaskioko.tvmaniac.app.test.graph

import com.thomaskioko.tvmaniac.app.di.ActivityGraph
import com.thomaskioko.tvmaniac.discover.presenter.di.DiscoverShowsTabGraph
import com.thomaskioko.tvmaniac.presenter.home.di.HomeScreenGraph
import com.thomaskioko.tvmaniac.presenter.showdetails.di.ShowDetailsScreenGraph

internal val ActivityGraph.homeScreenGraphFactory: HomeScreenGraph.Factory
    get() = this as HomeScreenGraph.Factory

internal val ActivityGraph.showDetailsScreenGraphFactory: ShowDetailsScreenGraph.Factory
    get() = this as ShowDetailsScreenGraph.Factory

internal val ActivityGraph.discoverShowsTabGraphFactory: DiscoverShowsTabGraph.Factory
    get() = this as DiscoverShowsTabGraph.Factory
