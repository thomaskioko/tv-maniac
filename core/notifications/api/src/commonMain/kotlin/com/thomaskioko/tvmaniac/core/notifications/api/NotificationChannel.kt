package com.thomaskioko.tvmaniac.core.notifications.api

public enum class NotificationChannel(public val id: String) {
    EPISODES_AIRING("episodes_airing"),
    DEVELOPER("developer"),
    ;

    public companion object {
        public fun fromId(id: String): NotificationChannel {
            return entries.firstOrNull { it.id == id } ?: EPISODES_AIRING
        }
    }
}
