package com.thomaskioko.tvmaniac.data.backup.api

public class BackupLocationUnreadableException(
    location: String,
    cause: Throwable? = null,
) : Exception("Cannot open $location", cause)
