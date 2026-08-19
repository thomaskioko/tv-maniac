package com.thomaskioko.tvmaniac.data.backup.api

public class BackupExportException(
    public val reason: BackupFailure,
    override val cause: Throwable? = null,
) : Exception("Backup export failed: $reason", cause)
