package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TraktFavoriteShowResponse(
    @SerialName("rank") val rank: Int,
    @SerialName("id") val id: Int,
    @SerialName("listed_at") val listedAt: String,
    @SerialName("notes") val notes: String? = null,
    @SerialName("type") val type: String,
    @SerialName("show") val show: ShowResponse,
)
