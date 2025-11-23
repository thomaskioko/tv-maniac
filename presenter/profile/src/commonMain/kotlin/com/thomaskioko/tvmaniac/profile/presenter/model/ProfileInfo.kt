package com.thomaskioko.tvmaniac.profile.presenter.model

public data class ProfileInfo(
    val slug: String,
    val username: String,
    val fullName: String?,
    val avatarUrl: String?,
    val stats: ProfileStats,
    val backgroundUrl: String?,
)
