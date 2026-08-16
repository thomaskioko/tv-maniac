package com.thomaskioko.tvmaniac.watchdateselection.nav

import com.thomaskioko.tvmaniac.episodes.api.WatchedDateTarget
import com.thomaskioko.tvmaniac.navigation.NavRoute
import com.thomaskioko.tvmaniac.navigation.OverlayRoute
import kotlinx.serialization.Serializable

@Serializable
public data class WatchDateSelectionParam(
    val target: WatchedDateTarget,
    val showId: Long,
    val episodeId: Long = 0,
    val seasonNumber: Long = 0,
    val episodeNumber: Long = 0,
    val markPrevious: Boolean = false,
    val isEdit: Boolean = false,
)

@Serializable
public data class WatchDateSelectionRoute(
    val param: WatchDateSelectionParam,
) : NavRoute, OverlayRoute
