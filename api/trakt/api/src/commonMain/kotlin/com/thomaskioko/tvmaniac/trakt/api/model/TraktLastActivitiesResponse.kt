package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TraktLastActivitiesResponse(
    @SerialName("all") val all: String,
    @SerialName("shows") val shows: TraktShowActivities,
    @SerialName("episodes") val episodes: TraktEpisodeActivities,
)

@Serializable
public data class TraktShowActivities(
    @SerialName("watched_at") val watchedAt: String? = null,
    @SerialName("collected_at") val collectedAt: String? = null,
    @SerialName("rated_at") val ratedAt: String? = null,
    @SerialName("watchlisted_at") val watchlistedAt: String? = null,
    @SerialName("favorited_at") val favoritedAt: String? = null,
    @SerialName("recommendations_at") val recommendationsAt: String? = null,
    @SerialName("commented_at") val commentedAt: String? = null,
    @SerialName("hidden_at") val hiddenAt: String? = null,
)

@Serializable
public data class TraktEpisodeActivities(
    @SerialName("watched_at") val watchedAt: String? = null,
    @SerialName("collected_at") val collectedAt: String? = null,
    @SerialName("rated_at") val ratedAt: String? = null,
    @SerialName("watchlisted_at") val watchlistedAt: String? = null,
    @SerialName("commented_at") val commentedAt: String? = null,
)
