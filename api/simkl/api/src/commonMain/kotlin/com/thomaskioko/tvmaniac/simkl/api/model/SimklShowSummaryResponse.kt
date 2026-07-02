package com.thomaskioko.tvmaniac.simkl.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class SimklShowSummaryResponse(
    @SerialName("ratings") val ratings: SimklRatings? = null,
)
