package com.thomaskioko.tvmaniac.trailers.nav

import com.thomaskioko.tvmaniac.navigation.NavRoute
import kotlinx.serialization.Serializable

@Serializable
public data class TrailersRoute(public val traktShowId: Long) : NavRoute
