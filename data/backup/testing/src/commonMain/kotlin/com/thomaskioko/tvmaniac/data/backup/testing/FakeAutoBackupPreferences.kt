package com.thomaskioko.tvmaniac.data.backup.testing

import com.thomaskioko.tvmaniac.data.backup.api.AutoBackupPreferences
import com.thomaskioko.tvmaniac.data.backup.api.model.AutoBackupStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

public class FakeAutoBackupPreferences : AutoBackupPreferences {

    private val status = MutableStateFlow(AutoBackupStatus())

    public fun setStatus(value: AutoBackupStatus) {
        status.value = value
    }

    public fun status(): AutoBackupStatus = status.value

    override fun observeStatus(): Flow<AutoBackupStatus> = status.asStateFlow()

    override suspend fun updateBackupTimestamp(timestamp: Long, failed: Boolean) {
        status.value = AutoBackupStatus(lastRunAt = timestamp, lastRunFailed = failed)
    }
}
