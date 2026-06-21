package com.thomaskioko.tvmaniac.discover.presenter.catalog

public sealed interface DiscoverCatalogAction

public data class CatalogShowClicked(val showId: Long) : DiscoverCatalogAction

public data object TrendingMoreClicked : DiscoverCatalogAction

public data object UpcomingMoreClicked : DiscoverCatalogAction

public data object PopularMoreClicked : DiscoverCatalogAction

public data object TopRatedMoreClicked : DiscoverCatalogAction
