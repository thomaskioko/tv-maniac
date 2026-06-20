package com.thomaskioko.tvmaniac.simkl.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class SimklUserStatsResponse(
    @SerialName("total_mins") val totalMins: Int? = null,
    @SerialName("tv") val tv: SimklStatsDomain? = null,
    @SerialName("anime") val anime: SimklStatsDomain? = null,
    @SerialName("movies") val movies: SimklMoviesStatsDomain? = null,
)

@Serializable
public data class SimklStatsDomain(
    @SerialName("total_mins") val totalMins: Int? = null,
    @SerialName("watching") val watching: SimklStatusBucket? = null,
    @SerialName("completed") val completed: SimklStatusBucket? = null,
    @SerialName("hold") val hold: SimklStatusBucket? = null,
    @SerialName("plantowatch") val plantowatch: SimklStatusBucket? = null,
)

@Serializable
public data class SimklStatusBucket(
    @SerialName("count") val count: Int? = null,
    @SerialName("watched_episodes_count") val watchedEpisodesCount: Int? = null,
    @SerialName("total_episodes_count") val totalEpisodesCount: Int? = null,
    @SerialName("left_to_watch_episodes") val leftToWatchEpisodes: Int? = null,
    @SerialName("left_to_watch_mins") val leftToWatchMins: Int? = null,
)

@Serializable
public data class SimklMoviesStatsDomain(
    @SerialName("total_mins") val totalMins: Int? = null,
    @SerialName("plantowatch") val plantowatch: SimklMoviesBucket? = null,
    @SerialName("completed") val completed: SimklMoviesBucket? = null,
    @SerialName("dropped") val dropped: SimklMoviesBucket? = null,
)

@Serializable
public data class SimklMoviesBucket(
    @SerialName("count") val count: Int? = null,
    @SerialName("mins") val mins: Int? = null,
)
