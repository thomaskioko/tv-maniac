package com.thomaskioko.tvmaniac.datastore.api

public object BackupFileName {
    public val Default: String = "tvmaniac-backup.json"
    private const val EXTENSION = ".json"
    private val FORBIDDEN = charArrayOf('/', '\\', ':', '*', '?', '"', '<', '>', '|')

    /**
     * @return the name a backup can be written under, or null when [name] cannot be used as one.
     */
    public fun sanitize(name: String): String? {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return null
        if (trimmed.any { it in FORBIDDEN }) return null
        if (trimmed == "." || trimmed == "..") return null
        return if (trimmed.endsWith(EXTENSION)) trimmed else "$trimmed$EXTENSION"
    }
}
