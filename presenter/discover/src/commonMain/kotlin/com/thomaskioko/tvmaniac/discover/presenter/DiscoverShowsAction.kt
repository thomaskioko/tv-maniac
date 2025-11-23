package com.thomaskioko.tvmaniac.discover.presenter

sealed interface DiscoverShowAction

data object UpComingClicked : DiscoverShowAction

data object TrendingClicked : DiscoverShowAction

data object PopularClicked : DiscoverShowAction

data object TopRatedClicked : DiscoverShowAction

data object RefreshData : DiscoverShowAction

data class ShowClicked(val id: Long) : DiscoverShowAction

data class MessageShown(val id: Long) : DiscoverShowAction

data class UpdateShowInLibrary(val id: Long, val inLibrary: Boolean) : DiscoverShowAction

data class NextEpisodeClicked(val showId: Long, val episodeId: Long) : DiscoverShowAction
