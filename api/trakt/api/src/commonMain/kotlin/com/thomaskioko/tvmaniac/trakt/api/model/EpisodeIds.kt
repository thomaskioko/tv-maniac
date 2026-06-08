package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class EpisodeIds(
    @SerialName("trakt") val trakt: Int,
    @SerialName("tmdb") val tmdb: Int?,
)
