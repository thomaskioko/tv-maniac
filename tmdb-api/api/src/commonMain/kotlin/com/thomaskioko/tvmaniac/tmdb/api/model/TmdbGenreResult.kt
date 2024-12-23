package com.thomaskioko.tvmaniac.tmdb.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmdbGenreResult(
  @SerialName("genres") var genres: ArrayList<GenreResponse>,
)
