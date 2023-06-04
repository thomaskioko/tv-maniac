package com.thomaskioko.tvmaniac.presentation.profile

sealed interface ProfileState

data class LoggedOutContent(
    val showTraktDialog: Boolean = false,
) : ProfileState

data class SignedInContent(
    val isLoading: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val loggedIn: Boolean = false,
    val traktUser: TraktUser? = null,
    val profileStats: ProfileStats? = null,
) : ProfileState

data class ProfileError(val error: String) : ProfileState
data class ProfileStatsError(val error: String) : ProfileState

data class TraktUser(
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
