package com.thomaskioko.tvmaniac.core.networkutil.api.model

public class SyncException(
    public val syncError: SyncError,
) : Exception(syncError.message)
