package com.thomaskioko.tvmaniac.data.backup.api

public interface BackupDestination {
    public fun write(contents: String)

    public fun read(): String
}
