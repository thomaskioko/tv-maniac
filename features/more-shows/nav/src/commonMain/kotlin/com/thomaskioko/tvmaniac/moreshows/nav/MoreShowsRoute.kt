package com.thomaskioko.tvmaniac.moreshows.nav

import com.thomaskioko.tvmaniac.navigation.NavRoute
import kotlinx.serialization.Serializable

@Serializable
public data class MoreShowsRoute(public val id: Long) : NavRoute
