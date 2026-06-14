package com.thomaskioko.tvmaniac.simkl.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class SimklCalendarEntry(
    @SerialName("date") val date: String,
    @SerialName("title") val title: String? = null,
    @SerialName("ep_title") val episodeTitle: String? = null,
    @SerialName("season") val season: Int? = null,
    @SerialName("episode") val episode: Int? = null,
    @SerialName("runtime") val runtime: Int? = null,
    @SerialName("ids") val ids: SimklCalendarIds? = null,
)

@Serializable
public data class SimklCalendarIds(
    @SerialName("simkl") val simkl: Long? = null,
    @SerialName("tmdb") val tmdb: String? = null,
    @SerialName("imdb") val imdb: String? = null,
)
