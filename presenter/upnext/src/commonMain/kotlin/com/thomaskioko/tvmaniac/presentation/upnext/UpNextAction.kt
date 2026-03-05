package com.thomaskioko.tvmaniac.presentation.upnext

import com.thomaskioko.tvmaniac.domain.upnext.model.UpNextSortOption

public sealed interface UpNextAction

public data class UpNextShowClicked(val showTraktId: Long) : UpNextAction

public data class MarkWatched(
    val showTraktId: Long,
    val episodeId: Long,
    val seasonNumber: Long,
    val episodeNumber: Long,
) : UpNextAction

public data class UpNextChangeSortOption(val sortOption: UpNextSortOption) : UpNextAction

public data object RefreshUpNext : UpNextAction

public data class UpNextMessageShown(val id: Long) : UpNextAction

public data class OpenShow(val showTraktId: Long) : UpNextAction

public data class OpenSeason(
    val showTraktId: Long,
    val seasonId: Long,
    val seasonNumber: Long,
) : UpNextAction

public data class UnfollowShow(val showTraktId: Long) : UpNextAction
