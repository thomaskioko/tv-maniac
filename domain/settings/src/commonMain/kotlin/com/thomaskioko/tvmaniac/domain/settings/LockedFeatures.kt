package com.thomaskioko.tvmaniac.domain.settings

public data class LockedFeatures(
    val backupLocked: Boolean = false,
    val customThemesLocked: Boolean = false,
    val posterStyleLocked: Boolean = false,
    val episodeNotificationsLocked: Boolean = false,
    val quickRateLocked: Boolean = false,
)
