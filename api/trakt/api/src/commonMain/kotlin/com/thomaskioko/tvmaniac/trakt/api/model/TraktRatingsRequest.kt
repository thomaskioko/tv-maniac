package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TraktRatingsRequest(
    @SerialName("shows") val shows: List<TraktShowRatingItem>? = null,
    @SerialName("seasons") val seasons: List<TraktSeasonRatingItem>? = null,
    @SerialName("episodes") val episodes: List<TraktEpisodeRatingItem>? = null,
)

@Serializable
public data class TraktShowRatingItem(
    @SerialName("rating") val rating: Int,
    @SerialName("ids") val ids: TraktShowIds,
)

@Serializable
public data class TraktSeasonRatingItem(
    @SerialName("rating") val rating: Int,
    @SerialName("ids") val ids: TraktSeasonIds,
)

@Serializable
public data class TraktEpisodeRatingItem(
    @SerialName("rating") val rating: Int,
    @SerialName("ids") val ids: TraktEpisodeIds,
)

@Serializable
public data class TraktSeasonIds(
    @SerialName("trakt") val traktId: Long? = null,
    @SerialName("tmdb") val tmdbId: Long? = null,
)

@Serializable
public data class TraktRemoveRatingsRequest(
    @SerialName("shows") val shows: List<TraktShowRatingIdItem>? = null,
    @SerialName("seasons") val seasons: List<TraktSeasonRatingIdItem>? = null,
    @SerialName("episodes") val episodes: List<TraktEpisodeRatingIdItem>? = null,
)

@Serializable
public data class TraktShowRatingIdItem(
    @SerialName("ids") val ids: TraktShowIds,
)

@Serializable
public data class TraktSeasonRatingIdItem(
    @SerialName("ids") val ids: TraktSeasonIds,
)

@Serializable
public data class TraktEpisodeRatingIdItem(
    @SerialName("ids") val ids: TraktEpisodeIds,
)
