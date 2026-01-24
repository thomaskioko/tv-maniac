package com.thomaskioko.tvmaniac.syncactivity.api.model

public enum class ActivityType(public val value: String) {
    SHOWS_WATCHLISTED("shows_watchlisted"),
    SHOWS_FAVORITED("shows_favorited"),
    EPISODES_WATCHED("episodes_watched"),
    ;

    public companion object {
        public fun fromValue(value: String): ActivityType? =
            entries.find { it.value == value }
    }
}
