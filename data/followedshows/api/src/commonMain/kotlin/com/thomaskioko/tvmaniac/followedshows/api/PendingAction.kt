package com.thomaskioko.tvmaniac.followedshows.api

public enum class PendingAction(public val value: String) {
    NOTHING("NOTHING"),
    UPLOAD("UPLOAD"),
    DELETE("DELETE"),

    /**
     * The delete was synced to Trakt; the local row is retained briefly to absorb pull lag and
     * prevent an eventually-consistent Trakt pull from resurrecting the row. Garbage collected by
     * `WatchedEpisodeDao.purgeSyncedDeletesOlderThan`.
     */
    SYNCED_DELETE("SYNCED_DELETE"),
    ;

    public companion object {
        public fun fromValue(value: String): PendingAction =
            entries.find { it.value == value } ?: NOTHING
    }
}
