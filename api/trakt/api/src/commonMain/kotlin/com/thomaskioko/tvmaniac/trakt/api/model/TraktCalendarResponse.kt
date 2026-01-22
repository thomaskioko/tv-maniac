package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TraktCalendarResponse(
    @SerialName("first_aired") val firstAired: String,
    @SerialName("episode") val episode: TraktCalendarEpisode,
    @SerialName("show") val show: TraktCalendarShow,
)

@Serializable
public data class TraktCalendarEpisode(
    @SerialName("season") val seasonNumber: Int,
    @SerialName("number") val episodeNumber: Int,
    @SerialName("title") val title: String?,
    @SerialName("ids") val ids: EpisodeIds,
    @SerialName("overview") val overview: String? = null,
    @SerialName("runtime") val runtime: Int? = null,
)

@Serializable
public data class TraktCalendarShow(
    @SerialName("title") val title: String,
    @SerialName("year") val year: Int? = null,
    @SerialName("ids") val ids: ShowIds,
)
