package com.thomaskioko.tvmaniac.data.backup.api.model

public data class AutoBackupStatus(
    val lastRunAt: Long? = null,
    val lastRunFailed: Boolean = false,
)
