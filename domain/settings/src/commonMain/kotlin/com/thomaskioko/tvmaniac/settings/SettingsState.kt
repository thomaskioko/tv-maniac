package com.thomaskioko.tvmaniac.settings

import com.thomaskioko.tvmaniac.datastore.api.Theme

sealed interface SettingsState

data class SettingsContent(
    val theme: Theme,
    val showPopup: Boolean,
    val loggedIn: Boolean,
    val showTraktDialog: Boolean,
    val traktUserName: String?,
    val traktFullName: String?,
    val traktUserPicUrl: String?,
) : SettingsState {
    companion object {
        val EMPTY = SettingsContent(
            theme = Theme.SYSTEM,
            showPopup = false,
            loggedIn = false,
            showTraktDialog = false,
            traktUserName = null,
            traktFullName = null,
            traktUserPicUrl = null,
        )
    }
}
