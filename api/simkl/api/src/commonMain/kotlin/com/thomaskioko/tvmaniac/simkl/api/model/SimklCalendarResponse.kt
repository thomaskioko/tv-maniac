package com.thomaskioko.tvmaniac.simkl.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class SimklCalendarEntry(
    @SerialName("date") val date: String,
    @SerialName("title") val title: String? = null,
    @SerialName("ids") val ids: SimklCalendarIds? = null,
    @SerialName("episode") val episode: SimklCalendarEpisode? = null,
)

@Serializable
public data class SimklCalendarEpisode(
    @SerialName("season") val season: Int? = null,
    @SerialName("episode") val episode: Int? = null,
)

@Serializable
public data class SimklCalendarIds(
    @SerialName("simkl_id") val simklId: Long? = null,
    @SerialName("tmdb") val tmdb: String? = null,
    @SerialName("imdb") val imdb: String? = null,
)
