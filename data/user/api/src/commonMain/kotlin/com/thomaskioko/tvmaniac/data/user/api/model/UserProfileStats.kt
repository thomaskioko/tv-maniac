package com.thomaskioko.tvmaniac.data.user.api.model

public data class UserProfileStats(
    val showsWatched: Long,
    val episodesWatched: Long,
    val showsWatchedLabel: String,
    val episodesWatchedLabel: String,
    val userWatchTime: UserWatchTime,
) {
    public companion object {
        public val Empty: UserProfileStats = UserProfileStats(
            showsWatched = 0,
            episodesWatched = 0,
            showsWatchedLabel = "0",
            episodesWatchedLabel = "0",
            userWatchTime = UserWatchTime(
                years = 0,
                days = 0,
                hours = 0,
                minutes = 0,
            ),
        )
    }
}
