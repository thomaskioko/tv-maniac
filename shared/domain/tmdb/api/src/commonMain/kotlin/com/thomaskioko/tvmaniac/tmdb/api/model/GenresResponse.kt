package com.thomaskioko.tvmaniac.remote.api.model

import com.thomaskioko.tvmaniac.tmdb.api.model.GenreResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenresResponse(
    @SerialName("genres") val genres: List<GenreResponse>
)
