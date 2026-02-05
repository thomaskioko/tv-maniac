package com.thomaskioko.tvmaniac.discover.presenter

public sealed interface DiscoverShowAction

public data object UpComingClicked : DiscoverShowAction

public data object TrendingClicked : DiscoverShowAction

public data object PopularClicked : DiscoverShowAction

public data object TopRatedClicked : DiscoverShowAction

public data object UpNextMoreClicked : DiscoverShowAction

public data object RefreshData : DiscoverShowAction

public data class ShowClicked(val traktId: Long) : DiscoverShowAction

public data class MessageShown(val id: Long) : DiscoverShowAction

public data class UpdateShowInLibrary(val traktId: Long, val inLibrary: Boolean) : DiscoverShowAction

public data class NextEpisodeClicked(val showTraktId: Long, val episodeId: Long) : DiscoverShowAction

public data class MarkNextEpisodeWatched(
    val showTraktId: Long,
    val episodeId: Long,
    val seasonNumber: Long,
    val episodeNumber: Long,
) : DiscoverShowAction

public data class UnfollowShowFromUpNext(val showTraktId: Long) : DiscoverShowAction

public data class OpenSeasonFromUpNext(
    val showTraktId: Long,
    val seasonId: Long,
    val seasonNumber: Long,
) : DiscoverShowAction
