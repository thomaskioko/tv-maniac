package com.thomaskioko.tvmaniac.domain.backup

import com.thomaskioko.tvmaniac.data.backup.api.BackupFailure
import com.thomaskioko.tvmaniac.data.backup.api.BackupResult
import com.thomaskioko.tvmaniac.data.backup.testing.FakeBackupRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class ExportBackupInteractorTest {

    private val repository = FakeBackupRepository()
    private val interactor = ExportBackupInteractor(repository)

    @Test
    fun `should write to the given location given a backup is exported`() = runTest {
        interactor(LOCATION)

        repository.lastWriteLocation shouldBe LOCATION
    }

    @Test
    fun `should return the counts given the backup is written`() = runTest {
        repository.setWriteResult(BackupResult.Success(showCount = 3, episodeCount = 42))

        interactor(LOCATION) shouldBe BackupResult.Success(showCount = 3, episodeCount = 42)
    }

    @Test
    fun `should return the reason given the backup cannot be written`() = runTest {
        repository.setWriteResult(BackupResult.Failed(BackupFailure.WriteFailed))

        interactor(LOCATION) shouldBe BackupResult.Failed(BackupFailure.WriteFailed)
    }

    private companion object {
        private const val LOCATION = "content://downloads/backup.json"
    }
}
