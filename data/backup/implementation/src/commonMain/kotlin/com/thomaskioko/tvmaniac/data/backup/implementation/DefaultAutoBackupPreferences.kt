package com.thomaskioko.tvmaniac.data.backup.implementation

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.thomaskioko.tvmaniac.core.base.DeviceLocalDataStore
import com.thomaskioko.tvmaniac.data.backup.api.AutoBackupPreferences
import com.thomaskioko.tvmaniac.data.backup.api.model.AutoBackupStatus
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultAutoBackupPreferences(
    @DeviceLocalDataStore private val dataStore: DataStore<Preferences>,
) : AutoBackupPreferences {

    override fun observeStatus(): Flow<AutoBackupStatus> = dataStore.data.map { preferences ->
        AutoBackupStatus(
            lastRunAt = preferences[KEY_LAST_RUN_AT],
            lastRunFailed = preferences[KEY_LAST_RUN_FAILED] ?: false,
        )
    }

    override suspend fun updateBackupTimestamp(timestamp: Long, failed: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_LAST_RUN_AT] = timestamp
            preferences[KEY_LAST_RUN_FAILED] = failed
        }
    }

    private companion object {
        private val KEY_LAST_RUN_AT: Preferences.Key<Long> = longPreferencesKey("auto_backup_last_run_at")
        private val KEY_LAST_RUN_FAILED: Preferences.Key<Boolean> =
            booleanPreferencesKey("auto_backup_last_run_failed")
    }
}

internal const val DEVICE_LOCAL_DATA_STORE_FILE_NAME: String = "tvmaniac-device-local.preferences_pb"
