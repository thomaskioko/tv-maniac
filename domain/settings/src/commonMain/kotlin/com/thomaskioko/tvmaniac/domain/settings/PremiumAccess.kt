package com.thomaskioko.tvmaniac.domain.settings

public data class PremiumAccess(
    val backup: Boolean = false,
    val customThemes: Boolean = false,
    val episodeNotifications: Boolean = false,
    val quickRate: Boolean = false,
)
