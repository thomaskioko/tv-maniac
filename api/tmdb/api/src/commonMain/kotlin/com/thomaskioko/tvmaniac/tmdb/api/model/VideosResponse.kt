package com.thomaskioko.tvmaniac.tmdb.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class VideosResponse(
    @SerialName("results") var results: ArrayList<VideoResultResponse>,
)

@Serializable
public data class VideoResultResponse(
    @SerialName("iso_639_1") var iso6391: String,
    @SerialName("iso_3166_1") var iso31661: String,
    @SerialName("name") var name: String,
    @SerialName("key") var key: String,
    @SerialName("site") var site: String,
    @SerialName("size") var size: Int,
    @SerialName("type") var type: String,
    @SerialName("official") var official: Boolean,
    @SerialName("id") var id: String,
)
