package com.thomaskioko.tvmaniac.data.user.api.model

public data class UserWatchTime(
    val years: Int,
    val days: Int,
    val hours: Int,
    val minutes: Int,
) {
    val totalDays: Int get() = years * 365 + days
    val months: Int get() = totalDays / 30
    val remainingDays: Int get() = totalDays % 30
}
