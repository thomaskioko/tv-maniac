package com.thomaskioko.tvmaniac.domain.backup

import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.data.backup.api.model.BackupFailure
import com.thomaskioko.tvmaniac.data.backup.api.model.BackupResult
import com.thomaskioko.tvmaniac.data.backup.testing.FakeAutoBackupPreferences
import com.thomaskioko.tvmaniac.data.backup.testing.FakeBackupRepository
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class RunAutoBackupInteractorTest {

    private val backupRepository = FakeBackupRepository()
    private val datastoreRepository = FakeDatastoreRepository()
    private val autoBackupPreferences = FakeAutoBackupPreferences()
    private val dateTimeProvider = FakeDateTimeProvider()

    private val interactor = RunAutoBackupInteractor(
        backupRepository = backupRepository,
        datastoreRepository = datastoreRepository,
        autoBackupPreferences = autoBackupPreferences,
        dateTimeProvider = dateTimeProvider,
        logger = FakeLogger(),
    )

    @Test
    fun `should write a backup to the chosen folder given one is set`() = runTest {
        datastoreRepository.saveBackupFolder(LOCATION)
        backupRepository.setWriteResult(BackupResult.Success(showCount = 3, episodeCount = 9))

        val outcome = interactor.executeSync(Unit)

        outcome shouldBe AutoBackupResult.Success(showCount = 3)
        backupRepository.lastWriteLocation shouldBe "$LOCATION/$FILE_NAME"
    }

    @Test
    fun `should write under the chosen name given one is set`() = runTest {
        datastoreRepository.saveBackupFolder(LOCATION)
        datastoreRepository.saveBackupFileName("my shows.json")
        backupRepository.setWriteResult(BackupResult.Success(showCount = 1, episodeCount = 1))

        interactor.executeSync(Unit)

        backupRepository.lastWriteLocation shouldBe "$LOCATION/my shows.json"
    }

    @Test
    fun `should record the run given the backup succeeds`() = runTest {
        datastoreRepository.saveBackupFolder(LOCATION)
        backupRepository.setWriteResult(BackupResult.Success(showCount = 1, episodeCount = 1))

        interactor.executeSync(Unit)

        autoBackupPreferences.status().lastRunFailed shouldBe false
        autoBackupPreferences.status().lastRunAt shouldBe dateTimeProvider.nowMillis()
    }

    @Test
    fun `should write nothing given no location has been chosen`() = runTest {
        val outcome = interactor.executeSync(Unit)

        outcome shouldBe AutoBackupResult.NoLocation
        backupRepository.lastWriteLocation.shouldBeNull()
        autoBackupPreferences.status().lastRunAt.shouldBeNull()
    }

    @Test
    fun `should forget the location given it can no longer be written`() = runTest {
        datastoreRepository.saveBackupFolder(LOCATION)
        backupRepository.setWriteResult(BackupResult.Failed(BackupFailure.LocationUnavailable))

        val outcome = interactor.executeSync(Unit)

        outcome shouldBe AutoBackupResult.LocationLost
        datastoreRepository.getBackupFolder().shouldBeNull()
        autoBackupPreferences.status().lastRunFailed shouldBe true
    }

    @Test
    fun `should keep the location given the write failed for another reason`() = runTest {
        datastoreRepository.saveBackupFolder(LOCATION)
        backupRepository.setWriteResult(BackupResult.Failed(BackupFailure.WriteFailed))

        val outcome = interactor.executeSync(Unit)

        outcome.shouldBeInstanceOf<AutoBackupResult.Failed>()
        datastoreRepository.getBackupFolder() shouldBe LOCATION
        autoBackupPreferences.status().lastRunFailed shouldBe true
    }

    @Test
    fun `should record a failure given the backup throws`() = runTest {
        datastoreRepository.saveBackupFolder(LOCATION)
        backupRepository.setCreateException(IllegalStateException("boom"))

        val outcome = interactor.executeSync(Unit)

        outcome.shouldBeInstanceOf<AutoBackupResult.Failed>()
        autoBackupPreferences.status().lastRunFailed shouldBe true
        datastoreRepository.getBackupFolder() shouldBe LOCATION
    }

    @Test
    fun `should write a backup on demand given the user asks for one`() = runTest {
        datastoreRepository.saveBackupFolder(LOCATION)
        backupRepository.setWriteResult(BackupResult.Success(showCount = 2, episodeCount = 4))

        val outcome = BackupNowInteractor(interactor).executeSync(Unit)

        outcome shouldBe AutoBackupResult.Success(showCount = 2)
        backupRepository.lastWriteLocation shouldBe "$LOCATION/$FILE_NAME"
    }

    private companion object {
        private const val FILE_NAME = "tvmaniac-backup.json"
        private const val LOCATION = "content://downloads/backup.json"
    }
}
