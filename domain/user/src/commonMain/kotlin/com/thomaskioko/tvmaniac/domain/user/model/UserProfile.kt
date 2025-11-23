package com.thomaskioko.tvmaniac.domain.user.model

import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState

public data class UserProfile(
    val slug: String,
    val username: String,
    val fullName: String?,
    val avatarUrl: String?,
    val backgroundUrl: String?,
    val stats: UserStats,
    val authState: TraktAuthState,
)
