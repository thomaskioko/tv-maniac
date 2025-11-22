package com.thomaskioko.tvmaniac.data.user.api.model

public data class UserProfile(
    val slug: String,
    val username: String,
    val fullName: String?,
    val avatarUrl: String?,
    val backgroundUrl: String?,
    val stats: UserProfileStats,
)
