package com.thomaskioko.tvmaniac.espisodedetails.nav.model

import com.thomaskioko.tvmaniac.navigation.NavRoute
import com.thomaskioko.tvmaniac.navigation.OverlayRoute
import kotlinx.serialization.Serializable

@Serializable
public data class EpisodeSheetParam(
    val episodeId: Long,
    val source: ScreenSource,
)

@Serializable
public data class EpisodeSheetRoute(
    val param: EpisodeSheetParam,
) : NavRoute, OverlayRoute
