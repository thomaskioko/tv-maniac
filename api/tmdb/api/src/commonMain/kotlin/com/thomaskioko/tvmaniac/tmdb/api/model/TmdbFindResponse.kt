package com.thomaskioko.tvmaniac.tmdb.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TmdbFindResponse(
    @SerialName("tv_results") val tvResults: List<TmdbFindTvResult> = emptyList(),
)

@Serializable
public data class TmdbFindTvResult(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
)
