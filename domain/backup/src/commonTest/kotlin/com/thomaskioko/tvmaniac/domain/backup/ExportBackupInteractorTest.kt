package com.thomaskioko.tvmaniac.domain.backup

import com.thomaskioko.tvmaniac.core.base.interactor.executeSync
import com.thomaskioko.tvmaniac.core.view.InvokeError
import com.thomaskioko.tvmaniac.core.view.InvokeSuccess
import com.thomaskioko.tvmaniac.data.backup.api.BackupExportException
import com.thomaskioko.tvmaniac.data.backup.api.BackupFailure
import com.thomaskioko.tvmaniac.data.backup.api.BackupResult
import com.thomaskioko.tvmaniac.data.backup.testing.FakeBackupRepository
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

internal class ExportBackupInteractorTest {

    private val repository = FakeBackupRepository()
    private val interactor = ExportBackupInteractor(repository)

    @Test
    fun `should write to the given location given a backup is exported`() = runTest {
        interactor.executeSync(ExportBackupInteractor.Params(LOCATION))

        repository.lastWriteLocation shouldBe LOCATION
    }

    @Test
    fun `should report success given the backup is written`() = runTest {
        repository.setWriteResult(BackupResult.Success(showCount = 3, episodeCount = 42))

        interactor(ExportBackupInteractor.Params(LOCATION)).toList().last() shouldBe InvokeSuccess
    }

    @Test
    fun `should report the reason given the backup cannot be written`() = runTest {
        repository.setWriteResult(BackupResult.Failed(BackupFailure.VerificationFailed))

        val failure = assertFailsWith<BackupExportException> {
            interactor.executeSync(ExportBackupInteractor.Params(LOCATION))
        }

        failure.reason shouldBe BackupFailure.VerificationFailed
    }

    @Test
    fun `should report an error given the repository throws`() = runTest {
        repository.setCreateException(IllegalStateException("database is locked"))

        val status = interactor(ExportBackupInteractor.Params(LOCATION)).toList().last()

        status.shouldBeInstanceOf<InvokeError>()
    }

    private companion object {
        private const val LOCATION = "content://downloads/backup.json"
    }
}
