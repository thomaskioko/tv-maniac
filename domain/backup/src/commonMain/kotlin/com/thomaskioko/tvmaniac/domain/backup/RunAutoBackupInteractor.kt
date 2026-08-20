package com.thomaskioko.tvmaniac.domain.backup

import com.thomaskioko.tvmaniac.core.base.interactor.ResultInteractor
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.data.backup.api.AutoBackupPreferences
import com.thomaskioko.tvmaniac.data.backup.api.BackupRepository
import com.thomaskioko.tvmaniac.data.backup.api.model.BackupFailure
import com.thomaskioko.tvmaniac.data.backup.api.model.BackupResult
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CancellationException

@Inject
@SingleIn(AppScope::class)
public class RunAutoBackupInteractor(
    private val backupRepository: BackupRepository,
    private val datastoreRepository: DatastoreRepository,
    private val autoBackupPreferences: AutoBackupPreferences,
    private val dateTimeProvider: DateTimeProvider,
    private val logger: Logger,
) : ResultInteractor<Unit, AutoBackupResult>() {

    override suspend fun doWork(params: Unit): AutoBackupResult {
        val location = datastoreRepository.getAutoBackupLocation()
        if (location == null) {
            logger.debug(TAG, "No location chosen, skipping automatic backup")
            return AutoBackupResult.NoLocation
        }

        val result = try {
            backupRepository.writeBackup(location)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (error: Exception) {
            logger.error(TAG, "Automatic backup failed: ${error.message}")
            recordAttempt(failed = true)
            return AutoBackupResult.Failed(error.message)
        }

        return when (result) {
            is BackupResult.Success -> {
                recordAttempt(failed = false)
                logger.debug(TAG, "Automatic backup wrote ${result.showCount} shows")
                AutoBackupResult.Success(showCount = result.showCount)
            }
            is BackupResult.Failed -> {
                recordAttempt(failed = true)
                if (result.reason == BackupFailure.LocationUnavailable) {
                    logger.warning(TAG, "Automatic backup location is gone, asking for a new one")
                    datastoreRepository.saveAutoBackupLocation(null)
                    return AutoBackupResult.LocationLost
                }
                logger.error(TAG, "Automatic backup failed: ${result.reason}")
                AutoBackupResult.Failed(result.reason.name)
            }
        }
    }

    private suspend fun recordAttempt(failed: Boolean) {
        autoBackupPreferences.updateBackupTimestamp(
            timestamp = dateTimeProvider.nowMillis(),
            failed = failed,
        )
    }

    private companion object {
        private const val TAG = "RunAutoBackup"
    }
}
