package com.thomaskioko.tvmaniac.datasource.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenresResponse(
    @SerialName("genres") val genres: List<GenreResponse>
)
