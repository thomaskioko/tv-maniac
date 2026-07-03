package com.thomaskioko.tvmaniac.data.ratings.api

import kotlinx.serialization.Serializable

@Serializable
public enum class RatingEntityType {
    SHOW,
    SEASON,
    EPISODE,
}
