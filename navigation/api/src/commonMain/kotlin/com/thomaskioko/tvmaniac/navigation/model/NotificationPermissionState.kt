package com.thomaskioko.tvmaniac.navigation.model

import kotlinx.serialization.Serializable

@Serializable
public data class NotificationPermissionState(
    val showRationale: Boolean = false,
    val requestPermission: Boolean = false,
)
