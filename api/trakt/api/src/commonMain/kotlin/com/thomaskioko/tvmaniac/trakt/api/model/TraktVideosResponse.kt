package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TraktVideosResponse(
    @SerialName("title") val title: String,
    @SerialName("url") val url: String,
    @SerialName("site") val site: String,
    @SerialName("type") val type: String,
    @SerialName("size") val size: Int? = null,
    @SerialName("official") val official: Boolean? = null,
    @SerialName("published_at") val publishedAt: String? = null,
    @SerialName("country") val country: String? = null,
    @SerialName("language") val language: String? = null,
)
