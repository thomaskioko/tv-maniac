package com.thomaskioko.tvmaniac.domain.user.model

import com.thomaskioko.tvmaniac.accountmanager.api.AccountAuthState

public data class UserProfile(
    val slug: String,
    val username: String,
    val fullName: String?,
    val avatarUrl: String?,
    val backgroundUrl: String?,
    val stats: UserStats,
    val authState: AccountAuthState,
    val statsLoaded: Boolean = true,
)
