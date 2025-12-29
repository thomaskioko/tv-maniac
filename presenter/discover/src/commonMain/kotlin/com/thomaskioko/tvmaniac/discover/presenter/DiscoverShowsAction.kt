package com.thomaskioko.tvmaniac.discover.presenter

public sealed interface DiscoverShowAction

public data object UpComingClicked : DiscoverShowAction

public data object TrendingClicked : DiscoverShowAction

public data object PopularClicked : DiscoverShowAction

public data object TopRatedClicked : DiscoverShowAction

public data object RefreshData : DiscoverShowAction

public data class ShowClicked(val id: Long) : DiscoverShowAction

public data class MessageShown(val id: Long) : DiscoverShowAction

public data class UpdateShowInLibrary(val id: Long, val inLibrary: Boolean) : DiscoverShowAction

public data class NextEpisodeClicked(val showId: Long, val episodeId: Long) : DiscoverShowAction

public data class MarkNextEpisodeWatched(
    val showId: Long,
    val episodeId: Long,
    val seasonNumber: Long,
    val episodeNumber: Long,
) : DiscoverShowAction

public data class UnfollowShowFromUpNext(val showId: Long) : DiscoverShowAction

public data class OpenSeasonFromUpNext(
    val showId: Long,
    val seasonId: Long,
    val seasonNumber: Long,
) : DiscoverShowAction
