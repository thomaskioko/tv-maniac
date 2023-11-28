package com.thomaskioko.tvmaniac.presentation.profile

data class ProfileState(
    val userInfo: UserInfo? = null,
    val errorMessage: String? = null,
    val showTraktDialog: Boolean = false,
    val isLoading: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val loggedIn: Boolean = false,
    val profileStats: ProfileStats? = null,
)

data class UserInfo(
    val slug: String,
    val userName: String?,
    val fullName: String?,
    val userPicUrl: String?,
)

data class ProfileStats(
    val showMonths: String,
    val showDays: String,
    val showHours: String,
    val collectedShows: String,
    val episodesWatched: String,
)
