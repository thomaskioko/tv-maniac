package com.thomaskioko.tvmaniac.app.test.graph

import com.thomaskioko.tvmaniac.app.di.ActivityGraph
import com.thomaskioko.tvmaniac.discover.presenter.di.DiscoverShowsTabGraph
import com.thomaskioko.tvmaniac.presenter.home.di.HomeChildGraph
import com.thomaskioko.tvmaniac.presenter.showdetails.di.ShowDetailsScreenGraph

internal val ActivityGraph.homeChildGraphFactory: HomeChildGraph.Factory
    get() = this as HomeChildGraph.Factory

internal val ActivityGraph.showDetailsScreenGraphFactory: ShowDetailsScreenGraph.Factory
    get() = this as ShowDetailsScreenGraph.Factory

internal val ActivityGraph.discoverShowsTabGraphFactory: DiscoverShowsTabGraph.Factory
    get() = this as DiscoverShowsTabGraph.Factory
