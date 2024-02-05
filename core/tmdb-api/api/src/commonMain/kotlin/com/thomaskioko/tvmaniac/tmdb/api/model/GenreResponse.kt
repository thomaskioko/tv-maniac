package com.thomaskioko.tvmaniac.tmdb.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenreResponse(
  @SerialName("id") var id: Int,
  @SerialName("name") var name: String,
)
