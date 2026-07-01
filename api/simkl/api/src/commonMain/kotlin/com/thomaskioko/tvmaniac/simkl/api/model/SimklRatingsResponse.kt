package com.thomaskioko.tvmaniac.simkl.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class SimklAddRatingsResponse(
    @SerialName("added") val added: SimklRatingsCountBucket? = null,
    @SerialName("not_found") val notFound: SimklRatingsNotFoundBucket? = null,
)

@Serializable
public data class SimklRemoveRatingsResponse(
    @SerialName("deleted") val deleted: SimklRatingsCountBucket? = null,
    @SerialName("not_found") val notFound: SimklRatingsNotFoundBucket? = null,
)

@Serializable
public data class SimklRatingsCountBucket(
    @SerialName("movies") val movies: Int? = null,
    @SerialName("shows") val shows: Int? = null,
)

@Serializable
public data class SimklRatingsNotFoundBucket(
    @SerialName("movies") val movies: List<SimklShowEntry> = emptyList(),
    @SerialName("shows") val shows: List<SimklShowEntry> = emptyList(),
)

@Serializable
public data class SimklRatings(
    @SerialName("simkl") val simkl: SimklRatingValue? = null,
)

@Serializable
public data class SimklRatingValue(
    @SerialName("rating") val rating: Double? = null,
    @SerialName("votes") val votes: Int? = null,
)

@Serializable
public data class SimklUserRatingsResponse(
    @SerialName("shows") val shows: List<SimklUserRatedShow> = emptyList(),
)

@Serializable
public data class SimklUserRatedShow(
    @SerialName("user_rating") val userRating: Int? = null,
    @SerialName("rated_at") val ratedAt: String? = null,
    @SerialName("show") val show: SimklShowEntry,
)
