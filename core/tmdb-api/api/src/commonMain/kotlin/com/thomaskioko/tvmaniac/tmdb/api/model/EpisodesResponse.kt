package com.thomaskioko.tvmaniac.tmdb.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EpisodesResponse(
    @SerialName("id") val id: Int,
    @SerialName("still_path") val imageUrl: String?,
)
