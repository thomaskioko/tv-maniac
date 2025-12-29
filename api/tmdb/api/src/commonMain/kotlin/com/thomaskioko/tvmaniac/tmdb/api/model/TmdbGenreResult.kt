package com.thomaskioko.tvmaniac.tmdb.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TmdbGenreResult(
    @SerialName("genres") var genres: ArrayList<GenreResponse>,
)
