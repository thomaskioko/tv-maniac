package com.thomaskioko.tvmaniac.ratingsheet.nav

import com.thomaskioko.tvmaniac.data.ratings.api.RatingEntityType
import com.thomaskioko.tvmaniac.navigation.NavRoute
import com.thomaskioko.tvmaniac.navigation.OverlayRoute
import kotlinx.serialization.Serializable

@Serializable
public data class RatingSheetParam(
    val ratingType: RatingEntityType,
    val id: Long,
)

@Serializable
public data class RatingSheetRoute(
    val param: RatingSheetParam,
) : NavRoute, OverlayRoute
