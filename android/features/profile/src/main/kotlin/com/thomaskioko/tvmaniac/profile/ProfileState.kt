package com.thomaskioko.tvmaniac.profile

sealed interface ProfileState

data class ProfileContent(
    val showTraktDialog: Boolean,
    val loggedIn: Boolean,
    val traktUser: TraktUser?,
    val profileStats: ProfileStats?
) : ProfileState {
    companion object {
        val EMPTY = ProfileContent(
            showTraktDialog = false,
            loggedIn = false,
            traktUser = null,
            profileStats = null
        )
    }
}

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
    val episodesWatched: String
)
