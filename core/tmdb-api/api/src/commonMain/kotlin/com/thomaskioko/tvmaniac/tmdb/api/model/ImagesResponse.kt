package com.thomaskioko.tvmaniac.tmdb.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImagesResponse(
    @SerialName("posters") var posters: ArrayList<Posters>,
)

@Serializable
data class Posters(
    @SerialName("aspect_ratio") var aspectRatio: Double,
    @SerialName("height") var height: Int,
    @SerialName("file_path") var filePath: String? = null,
    @SerialName("vote_average") var voteAverage: Double,
    @SerialName("vote_count") var voteCount: Int,
    @SerialName("width") var width: Int,
)
