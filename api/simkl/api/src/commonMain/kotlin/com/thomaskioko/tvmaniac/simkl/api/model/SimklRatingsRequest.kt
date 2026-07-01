package com.thomaskioko.tvmaniac.simkl.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class SimklRatingsRequest(
    @SerialName("shows") val shows: List<SimklRatingItem>? = null,
)

@Serializable
public data class SimklRatingItem(
    @SerialName("rating") val rating: Int,
    @SerialName("rated_at") val ratedAt: String? = null,
    @SerialName("ids") val ids: SimklShowIds,
)

@Serializable
public data class SimklRemoveRatingsRequest(
    @SerialName("shows") val shows: List<SimklRatingIdItem>? = null,
)

@Serializable
public data class SimklRatingIdItem(
    @SerialName("ids") val ids: SimklShowIds,
)
