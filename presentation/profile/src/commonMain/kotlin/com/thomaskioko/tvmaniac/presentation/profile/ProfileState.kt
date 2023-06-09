package com.thomaskioko.tvmaniac.presentation.profile

sealed interface ProfileState {
    val userInfo: UserInfo?
    val errorMessage: String?
    val showTraktDialog: Boolean
}

data class LoggedOutContent(
    override val userInfo: UserInfo? = null,
    override val errorMessage: String? = null,
    override val showTraktDialog: Boolean = false,
) : ProfileState {
    companion object {
        val DEFAULT_STATE = LoggedOutContent(
            userInfo = null,
            errorMessage = null,
            showTraktDialog = false,
        )
    }
}

data class LoggedInContent(
    override val userInfo: UserInfo? = null,
    override val errorMessage: String? = null,
    override val showTraktDialog: Boolean = false,
    val isLoading: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val loggedIn: Boolean = false,
    val profileStats: ProfileStats? = null,
) : ProfileState

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
