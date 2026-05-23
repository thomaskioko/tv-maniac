package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TraktListItemResponse(
    @SerialName("listed_at") val listedAt: String,
    @SerialName("type") val type: String,
    @SerialName("show") val show: ShowResponse? = null,
)
