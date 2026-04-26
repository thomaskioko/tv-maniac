package com.thomaskioko.tvmaniac.app.test.graph

import com.thomaskioko.tvmaniac.app.di.ActivityGraph
import com.thomaskioko.tvmaniac.discover.presenter.di.DiscoverShowsTabGraph
import com.thomaskioko.tvmaniac.home.nav.TabDestination
import com.thomaskioko.tvmaniac.home.nav.di.TabDestinationMultibindings
import com.thomaskioko.tvmaniac.presenter.home.di.HomeScreenGraph
import com.thomaskioko.tvmaniac.presenter.showdetails.di.ShowDetailsScreenGraph

internal val ActivityGraph.homeScreenGraphFactory: HomeScreenGraph.Factory
    get() = this as HomeScreenGraph.Factory

internal val ActivityGraph.showDetailsScreenGraphFactory: ShowDetailsScreenGraph.Factory
    get() = this as ShowDetailsScreenGraph.Factory

internal val HomeScreenGraph.discoverShowsTabGraphFactory: DiscoverShowsTabGraph.Factory
    get() = this as DiscoverShowsTabGraph.Factory

internal val HomeScreenGraph.tabDestinations: Set<TabDestination>
    get() = (this as TabDestinationMultibindings).tabDestinations()
