package com.thomaskioko.tvmaniac.data.backup.api

public class RestoreFailedException(
    public val reason: RestoreFailure,
    override val cause: Throwable? = null,
) : Exception("Backup restore failed: $reason", cause)
