package com.thomaskioko.tvmaniac.followedshows.api

public enum class PendingAction(public val value: String) {
    NOTHING("NOTHING"),
    UPLOAD("UPLOAD"),
    DELETE("DELETE"),
    ;

    public companion object {
        public fun fromValue(value: String): PendingAction =
            entries.find { it.value == value } ?: NOTHING
    }
}
