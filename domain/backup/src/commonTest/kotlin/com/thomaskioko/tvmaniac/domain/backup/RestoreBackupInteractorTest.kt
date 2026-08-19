package com.thomaskioko.tvmaniac.domain.backup

import com.thomaskioko.tvmaniac.core.base.interactor.executeSync
import com.thomaskioko.tvmaniac.core.tasks.testing.FakeBackgroundTaskScheduler
import com.thomaskioko.tvmaniac.data.backup.api.RestoreFailure
import com.thomaskioko.tvmaniac.data.backup.api.RestoreResult
import com.thomaskioko.tvmaniac.data.backup.testing.FakeBackupRepository
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class RestoreBackupInteractorTest {

    private val repository = FakeBackupRepository()
    private val scheduler = FakeBackgroundTaskScheduler()
    private val interactor = RestoreBackupInteractor(repository, scheduler)

    @Test
    fun `should schedule the metadata refill given a backup is restored`() = runTest {
        interactor.executeSync(RestoreBackupInteractor.Params(LOCATION))

        scheduler.getScheduledRequests().map { it.id } shouldBe listOf(RestoredShowsRefillWorker.WORKER_NAME)
    }

    @Test
    fun `should schedule nothing given the restore fails`() = runTest {
        repository.setRestoreResult(RestoreResult.Failed(RestoreFailure.SyncInProgress))

        interactor.executeSync(RestoreBackupInteractor.Params(LOCATION))

        scheduler.getScheduledRequests().shouldBeEmpty()
    }

    private companion object {
        private const val LOCATION = "content://downloads/backup.json"
    }
}
