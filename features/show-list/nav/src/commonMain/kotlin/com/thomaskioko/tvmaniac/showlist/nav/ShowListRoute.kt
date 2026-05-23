package com.thomaskioko.tvmaniac.showlist.nav

import com.thomaskioko.tvmaniac.navigation.NavRoute
import com.thomaskioko.tvmaniac.navigation.OverlayRoute
import kotlinx.serialization.Serializable

@Serializable
public data class ShowListParam(
    val showId: Long,
)

@Serializable
public data class ShowListRoute(
    val param: ShowListParam,
) : NavRoute, OverlayRoute
