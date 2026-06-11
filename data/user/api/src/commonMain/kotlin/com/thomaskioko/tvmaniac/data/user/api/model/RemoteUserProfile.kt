package com.thomaskioko.tvmaniac.data.user.api.model

public data class RemoteUserProfile(
    val slug: String,
    val username: String,
    val fullName: String?,
    val avatarUrl: String?,
    val backgroundUrl: String?,
)
