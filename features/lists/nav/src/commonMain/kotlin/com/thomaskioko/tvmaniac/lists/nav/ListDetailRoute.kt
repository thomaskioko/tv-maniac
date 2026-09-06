package com.thomaskioko.tvmaniac.lists.nav

import com.thomaskioko.tvmaniac.lists.nav.model.ListDetailParam
import com.thomaskioko.tvmaniac.navigation.NavRoute
import kotlinx.serialization.Serializable

@Serializable
public data class ListDetailRoute(public val param: ListDetailParam) : NavRoute
