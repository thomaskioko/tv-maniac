package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TraktRatingResponse(
    @SerialName("rating") val rating: Double,
    @SerialName("votes") val votes: Long,
    @SerialName("distribution") val distribution: Map<String, Int>,
)

@Serializable
public data class TraktUserRatingItem(
    @SerialName("rated_at") val ratedAt: String,
    @SerialName("rating") val rating: Int,
    @SerialName("type") val type: String,
    @SerialName("show") val show: TraktHistoryShow,
)
