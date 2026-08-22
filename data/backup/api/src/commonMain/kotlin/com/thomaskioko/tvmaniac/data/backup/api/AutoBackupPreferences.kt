package com.thomaskioko.tvmaniac.data.backup.api

import com.thomaskioko.tvmaniac.data.backup.api.model.AutoBackupStatus
import kotlinx.coroutines.flow.Flow

/**
 * When an automatic backup last ran, and whether it worked.
 *
 * Kept apart from the app's other preferences because it must not follow the user to a new device.
 * A restored device inheriting this would claim a backup it never took.
 */
public interface AutoBackupPreferences {
    public fun observeStatus(): Flow<AutoBackupStatus>

    public suspend fun updateBackupTimestamp(timestamp: Long, failed: Boolean = false)
}
