package com.thomaskioko.tvmaniac.episodes.api

public enum class WatchedEpisodeSyncOperation(public val value: String) {
    NOTHING("NOTHING"),
    UPLOAD("UPLOAD"),
    DELETE("DELETE"),
    UPDATE("UPDATE"),
}
