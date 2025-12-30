package com.thomaskioko.tvmaniac.tmdb.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class ImagesResponse(
    @SerialName("posters") var posters: ArrayList<Posters>,
)

@Serializable
public data class Posters(
    @SerialName("aspect_ratio") var aspectRatio: Double,
    @SerialName("height") var height: Int,
    @SerialName("file_path") var filePath: String,
    @SerialName("vote_average") var voteAverage: Double,
    @SerialName("vote_count") var voteCount: Int,
    @SerialName("width") var width: Int,
)
