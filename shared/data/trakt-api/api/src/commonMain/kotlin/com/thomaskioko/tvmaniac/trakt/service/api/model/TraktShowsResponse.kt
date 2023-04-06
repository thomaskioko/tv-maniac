package com.thomaskioko.tvmaniac.trakt.service.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TraktShowsResponse(
    @SerialName("watchers") val watchers: Int? = null,
    @SerialName("show") val show: TraktShowResponse
)

@Serializable
data class TraktShowResponse(
    @SerialName("title") val title: String,
    @SerialName("year") val year: String?,
    @SerialName("ids") val ids: ShowIds,
    @SerialName("overview") val overview: String?,
    @SerialName("language") val language: String?,
    @SerialName("first_aired") val firstAirDate: String?,
    @SerialName("runtime") val runtime: Int,
    @SerialName("status") val status: String,
    @SerialName("rating") val rating: Double,
    @SerialName("votes") val votes: Int,
    @SerialName("aired_episodes") val airedEpisodes: Int,
    @SerialName("genres") val genres: List<String>,
)

@Serializable
data class ShowIds(
    @SerialName("trakt") val trakt: Int,
    @SerialName("tmdb") val tmdb: Int?,
    @SerialName("slug") val slug: String,
)