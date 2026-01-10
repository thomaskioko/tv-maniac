package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TraktShowsResponse(
    @SerialName("watchers") val watchers: Long? = null,
    @SerialName("watcher_count") val watcherCount: Long? = null,
    @SerialName("play_count") val playCount: Long? = null,
    @SerialName("collected_count") val collectedCount: Long? = null,
    @SerialName("collector_count") val collectorCount: Long? = null,
    @SerialName("user_count") val userCount: Long? = null,
    @SerialName("show") val show: TraktShowResponse,
)

@Serializable
public data class TraktShowResponse(
    @SerialName("title") val title: String,
    @SerialName("year") val year: Long? = null,
    @SerialName("ids") val ids: ShowIds,
    @SerialName("tagline") val tagline: String? = null,
    @SerialName("overview") val overview: String? = null,
    @SerialName("language") val language: String? = null,
    @SerialName("first_aired") val firstAirDate: String? = null,
    @SerialName("runtime") val runtime: Long? = null,
    @SerialName("country") val country: String? = null,
    @SerialName("trailer") val trailer: String? = null,
    @SerialName("homepage") val homepage: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("rating") val rating: Double? = null,
    @SerialName("votes") val votes: Long? = null,
    @SerialName("aired_episodes") val airedEpisodes: Long? = null,
    @SerialName("genres") val genres: List<String>? = null,
    @SerialName("certification") val certification: String? = null,
    @SerialName("network") val network: String? = null,
    @SerialName("airs") val airs: Airs? = null,
)

@Serializable
public data class ShowIds(
    @SerialName("trakt") val trakt: Long,
    @SerialName("tmdb") val tmdb: Long? = null,
    @SerialName("slug") val slug: String = "",
    @SerialName("imdb") val imdb: String? = null,
    @SerialName("tvdb") val tvdb: Long? = null,
)

@Serializable
public data class Airs(
    @SerialName("day") val day: String? = null,
    @SerialName("time") val time: String? = null,
    @SerialName("timezone") val timezone: String? = null,
)

@Serializable
public data class TraktSearchResult(
    @SerialName("type") val type: String,
    @SerialName("score") val score: Double? = null,
    @SerialName("show") val show: TraktShowResponse? = null,
)
