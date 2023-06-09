package com.thomaskioko.tvmaniac.presentation.settings

import com.thomaskioko.tvmaniac.datastore.api.Theme

sealed interface SettingsState {
    val userInfo: UserInfo?
    val theme: Theme
    val showTraktDialog: Boolean
    val showPopup: Boolean
    val errorMessage: String?
}

data class Default(
    override val userInfo: UserInfo?,
    override val theme: Theme,
    override val showTraktDialog: Boolean,
    override val showPopup: Boolean,
    override val errorMessage: String?,
) : SettingsState {
    companion object {
        val EMPTY = Default(
            userInfo = null,
            theme = Theme.SYSTEM,
            showTraktDialog = false,
            showPopup = false,
            errorMessage = null,
        )
    }
}

data class LoggedInContent(
    override val userInfo: UserInfo?,
    override val theme: Theme,
    override val showTraktDialog: Boolean,
    override val showPopup: Boolean,
    override val errorMessage: String?,
    val showLogoutDialog: Boolean,
    val isLoading: Boolean,
) : SettingsState {
    companion object {
        val DEFAULT_STATE = LoggedInContent(
            userInfo = null,
            theme = Theme.SYSTEM,
            showTraktDialog = false,
            showPopup = false,
            isLoading = false,
            errorMessage = null,
            showLogoutDialog = true,
        )
    }
}

data class UserInfo(
    val slug: String,
    val userName: String?,
    val fullName: String?,
    val userPicUrl: String?,
)
