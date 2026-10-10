package com.thomaskioko.tvmaniac.genreshows.nav

import com.thomaskioko.tvmaniac.genreshows.nav.model.GenreShowsParam
import com.thomaskioko.tvmaniac.navigation.NavRoute
import kotlinx.serialization.Serializable

@Serializable
public data class GenreShowsRoute(public val param: GenreShowsParam) : NavRoute
