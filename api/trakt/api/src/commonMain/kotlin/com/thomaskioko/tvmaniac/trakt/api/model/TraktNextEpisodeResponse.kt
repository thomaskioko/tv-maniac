package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TraktNextEpisodeResponse(
    @SerialName("season") val seasonNumber: Int,
    @SerialName("number") val episodeNumber: Int,
    @SerialName("title") val title: String? = null,
    @SerialName("ids") val ids: EpisodeIds,
    @SerialName("overview") val overview: String? = null,
    @SerialName("rating") val rating: Double? = null,
    @SerialName("votes") val votes: Int? = null,
    @SerialName("runtime") val runtime: Int? = null,
    @SerialName("first_aired") val firstAired: String? = null,
)
