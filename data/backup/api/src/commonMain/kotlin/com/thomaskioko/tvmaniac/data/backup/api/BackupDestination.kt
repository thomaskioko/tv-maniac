package com.thomaskioko.tvmaniac.data.backup.api

public interface BackupDestination {
    public fun write(location: String, contents: String)

    public fun read(location: String): String

    public fun safetyCopyLocation(): String
}
