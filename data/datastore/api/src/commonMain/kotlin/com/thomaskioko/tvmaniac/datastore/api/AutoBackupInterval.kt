package com.thomaskioko.tvmaniac.datastore.api

public enum class AutoBackupInterval(public val days: Int) {
    DAILY(days = 1),
    WEEKLY(days = 7),
    FORTNIGHTLY(days = 14),
    MONTHLY(days = 30),
    ;

    public companion object {
        public val Default: AutoBackupInterval = WEEKLY

        public fun fromName(name: String?): AutoBackupInterval =
            entries.firstOrNull { it.name == name } ?: Default
    }
}
