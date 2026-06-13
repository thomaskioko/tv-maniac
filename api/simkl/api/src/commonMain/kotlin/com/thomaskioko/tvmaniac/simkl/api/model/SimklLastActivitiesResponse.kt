package com.thomaskioko.tvmaniac.simkl.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class SimklLastActivitiesResponse(
    @SerialName("tv_shows") val tvShows: SimklShowActivities? = null,
)

@Serializable
public data class SimklShowActivities(
    @SerialName("all") val all: String? = null,
    @SerialName("plantowatch") val planToWatch: String? = null,
    @SerialName("watching") val watching: String? = null,
    @SerialName("hold") val hold: String? = null,
)
