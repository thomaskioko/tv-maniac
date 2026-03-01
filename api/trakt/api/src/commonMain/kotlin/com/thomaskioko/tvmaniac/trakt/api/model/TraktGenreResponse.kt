package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TraktGenreResponse(
    @SerialName("name") val name: String,
    @SerialName("slug") val slug: String,
)
