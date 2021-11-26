package com.thomaskioko.tvmaniac.datasource.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TvShowsResponse(
    @SerialName("page") val page: Int,
    @SerialName("results") val results: List<ShowResponse>,
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("total_results") val totalResults: Int
)
