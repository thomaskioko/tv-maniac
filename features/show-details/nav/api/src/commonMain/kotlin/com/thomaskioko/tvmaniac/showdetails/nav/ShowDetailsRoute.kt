package com.thomaskioko.tvmaniac.showdetails.nav

import com.thomaskioko.tvmaniac.navigation.NavRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import kotlinx.serialization.Serializable

@Serializable
public data class ShowDetailsRoute(public val param: ShowDetailsParam) : NavRoute
