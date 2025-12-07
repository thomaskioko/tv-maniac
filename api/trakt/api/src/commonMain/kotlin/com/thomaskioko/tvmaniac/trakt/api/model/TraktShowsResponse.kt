package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TraktShowsResponse(
    @SerialName("watchers") val watchers: Int? = null,
    @SerialName("watcher_count") val watcherCount: Int? = null,
    @SerialName("play_count") val playCount: Int? = null,
    @SerialName("collected_count") val collectedCount: Int? = null,
    @SerialName("collector_count") val collectorCount: Int? = null,
    @SerialName("user_count") val userCount: Int? = null,
    @SerialName("show") val show: TraktShowResponse,
)

@Serializable
data class TraktShowResponse(
    @SerialName("title") val title: String,
    @SerialName("year") val year: Int? = null,
    @SerialName("ids") val ids: ShowIds,
    @SerialName("tagline") val tagline: String? = null,
    @SerialName("overview") val overview: String? = null,
    @SerialName("language") val language: String? = null,
    @SerialName("first_aired") val firstAirDate: String? = null,
    @SerialName("runtime") val runtime: Int? = null,
    @SerialName("country") val country: String? = null,
    @SerialName("trailer") val trailer: String? = null,
    @SerialName("homepage") val homepage: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("rating") val rating: Double? = null,
    @SerialName("votes") val votes: Int? = null,
    @SerialName("aired_episodes") val airedEpisodes: Int? = null,
    @SerialName("genres") val genres: List<String>? = null,
    @SerialName("certification") val certification: String? = null,
    @SerialName("network") val network: String? = null,
    @SerialName("airs") val airs: Airs? = null,
    @SerialName("images") val images: TraktImages? = null,
)

@Serializable
data class ShowIds(
    @SerialName("trakt") val trakt: Int,
    @SerialName("slug") val slug: String = "",
    @SerialName("imdb") val imdb: String? = null,
    @SerialName("tmdb") val tmdb: Int? = null,
    @SerialName("tvdb") val tvdb: Int? = null,
)

@Serializable
data class Airs(
    @SerialName("day") val day: String? = null,
    @SerialName("time") val time: String? = null,
    @SerialName("timezone") val timezone: String? = null,
)

@Serializable
data class TraktImages(
    @SerialName("fanart") val fanart: List<String> = emptyList(),
    @SerialName("poster") val poster: List<String> = emptyList(),
    @SerialName("logo") val logo: List<String> = emptyList(),
    @SerialName("banner") val banner: List<String> = emptyList(),
    @SerialName("thumb") val thumb: List<String> = emptyList(),
    @SerialName("clearart") val clearart: List<String> = emptyList(),
)

@Serializable
data class TraktSearchResult(
    @SerialName("type") val type: String,
    @SerialName("score") val score: Double? = null,
    @SerialName("show") val show: TraktShowResponse? = null,
)
