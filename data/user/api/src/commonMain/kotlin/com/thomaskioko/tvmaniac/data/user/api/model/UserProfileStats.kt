package com.thomaskioko.tvmaniac.data.user.api.model

public data class UserProfileStats(
    val showsWatched: Long,
    val episodesWatched: Long,
    val userWatchTime: UserWatchTime,
) {
    public companion object {
        public val Empty: UserProfileStats = UserProfileStats(
            showsWatched = 0,
            episodesWatched = 0,
            userWatchTime = UserWatchTime(
                years = 0,
                days = 0,
                hours = 0,
                minutes = 0,
            ),
        )
    }
}
