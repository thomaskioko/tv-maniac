package com.thomaskioko.tvmaniac.seasondetails.nav

import com.thomaskioko.tvmaniac.navigation.NavRoute
import kotlinx.serialization.Serializable

@Serializable
public data class SeasonDetailsRoute(public val param: SeasonDetailsUiParam) : NavRoute
