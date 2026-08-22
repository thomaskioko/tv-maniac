package com.thomaskioko.tvmaniac.episodes.api

import kotlinx.serialization.Serializable

@Serializable
public enum class WatchedDateTarget {
    EPISODE,
    SEASON,
    SHOW,
}
