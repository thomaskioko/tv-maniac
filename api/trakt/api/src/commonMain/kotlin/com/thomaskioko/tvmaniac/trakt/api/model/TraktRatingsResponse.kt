package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TraktAddRatingsResponse(
    @SerialName("added") val added: TraktRatingsCount,
    @SerialName("not_found") val notFound: TraktRatingsNotFound,
)

@Serializable
public data class TraktRemoveRatingsResponse(
    @SerialName("deleted") val deleted: TraktRatingsCount,
    @SerialName("not_found") val notFound: TraktRatingsNotFound,
)

@Serializable
public data class TraktRatingsCount(
    @SerialName("movies") val movies: Int? = null,
    @SerialName("shows") val shows: Int? = null,
    @SerialName("seasons") val seasons: Int? = null,
    @SerialName("episodes") val episodes: Int? = null,
)

@Serializable
public data class TraktRatingsNotFound(
    @SerialName("movies") val movies: List<TraktShowRatingIdItem>? = null,
    @SerialName("shows") val shows: List<TraktShowRatingIdItem>? = null,
    @SerialName("seasons") val seasons: List<TraktSeasonRatingIdItem>? = null,
    @SerialName("episodes") val episodes: List<TraktEpisodeRatingIdItem>? = null,
)
