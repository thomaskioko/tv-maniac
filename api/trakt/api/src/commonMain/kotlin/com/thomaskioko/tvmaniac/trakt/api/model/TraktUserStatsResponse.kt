package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TraktUserStatsResponse(
  @SerialName("movies") val movies: Movies,
  @SerialName("shows") val shows: Shows,
  @SerialName("seasons") val seasons: Seasons,
  @SerialName("episodes") val episodes: Episodes,
)

@Serializable
data class Movies(
  @SerialName("plays") val plays: Int,
  @SerialName("watched") val watched: Int,
  @SerialName("minutes") val minutes: Int,
  @SerialName("collected") val collected: Int,
)

@Serializable
data class Shows(
  @SerialName("watched") val watched: Int,
  @SerialName("collected") val collected: Int,
)

@Serializable
data class Seasons(
  @SerialName("ratings") val ratings: Int,
)

@Serializable
data class Episodes(
  @SerialName("plays") val plays: Int,
  @SerialName("watched") val watched: Int,
  @SerialName("minutes") val minutes: Int,
  @SerialName("collected") val collected: Int,
)
