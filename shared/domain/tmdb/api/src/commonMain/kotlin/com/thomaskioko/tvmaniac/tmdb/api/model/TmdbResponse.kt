package com.thomaskioko.tvmaniac.tmdb.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmdbResponse(
    @SerialName("page") val page: Int,
    @SerialName("results") val results: List<TmdbShowResponse>,
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("total_results") val totalResults: Int
)
