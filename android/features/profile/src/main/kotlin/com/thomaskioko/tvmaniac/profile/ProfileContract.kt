package com.thomaskioko.tvmaniac.profile

import com.thomaskioko.tvmaniac.shared.core.ui.Action
import com.thomaskioko.tvmaniac.shared.core.ui.Effect

sealed class ProfileState


data class ProfileStateContent(
    val showTraktDialog: Boolean,
    val loggedIn: Boolean,
    val traktUser: TraktUser?,
    val profileStats: ProfileStats?
) : ProfileState() {

    data class TraktUser(
        val userName: String,
        val fullName: String?,
        val userPicUrl: String?,
    )

    data class ProfileStats(
        val showMonths: String,
        val showDays: String,
        val showHours: String,
        val collectedShows: String,
        val episodesWatched: String
    )

    companion object {
        val EMPTY = ProfileStateContent(
            showTraktDialog = false,
            loggedIn = false,
            traktUser = null,
            profileStats = null
        )
    }
}

sealed class ProfileActions : Action {

    object ShowTraktDialog : ProfileActions()
    object DismissTraktDialog : ProfileActions()
    object TraktLogout : ProfileActions()
    object TraktLogin : ProfileActions()
    object RefreshTraktAuthToken : ProfileActions()
    object FetchTraktUserProfile : ProfileActions()
}

sealed class ProfileEffect : Effect
