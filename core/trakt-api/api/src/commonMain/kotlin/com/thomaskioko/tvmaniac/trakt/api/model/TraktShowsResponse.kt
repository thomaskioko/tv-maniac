package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TraktShowsResponse(
    @SerialName("watchers") val watchers: Int? = null,
    @SerialName("show") val show: TraktShowResponse,
)

@Serializable
data class TraktShowResponse(
    @SerialName("title") val title: String,
    @SerialName("year") val year: Int? = null,
    @SerialName("ids") val ids: ShowIds,
    @SerialName("overview") val overview: String? = null,
    @SerialName("language") val language: String? = null,
    @SerialName("first_aired") val firstAirDate: String? = null,
    @SerialName("runtime") val runtime: Int,
    @SerialName("status") val status: String,
    @SerialName("rating") val rating: Double,
    @SerialName("votes") val votes: Int,
    @SerialName("aired_episodes") val airedEpisodes: Int,
    @SerialName("genres") val genres: List<String>,
    @SerialName("airs") val airs: Airs? = null,
)

@Serializable
data class ShowIds(
    @SerialName("trakt") val trakt: Int,
    @SerialName("tmdb") val tmdb: Int? = null,
    @SerialName("slug") val slug: String,
)

@Serializable
data class Airs(
    @SerialName("day") val day: String? = null,
    @SerialName("time") val time: String? = null,
    @SerialName("timezone") val timezone: String? = null,
)
