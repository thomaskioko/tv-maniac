package com.thomaskioko.tvmaniac.core.base.model

import kotlinx.serialization.Serializable

@Serializable
data class Configs(
    val isDebug: Boolean,
    val tmdbApiKey: String,
    val traktClientId: String,
    val traktClientSecret: String,
    val traktRedirectUri: String,
)
