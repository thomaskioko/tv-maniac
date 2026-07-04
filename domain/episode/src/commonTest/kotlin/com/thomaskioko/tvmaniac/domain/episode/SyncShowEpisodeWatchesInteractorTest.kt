package com.thomaskioko.tvmaniac.domain.episode

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SyncShowEpisodeWatchesInteractorTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val watchedEpisodeSyncRepository = FakeWatchedEpisodeSyncRepository()

    private val interactor = SyncShowEpisodeWatchesInteractor(
        watchedEpisodeSyncRepository = watchedEpisodeSyncRepository,
        dispatchers = dispatchers,
    )

    @Test
    fun `should sync watch status for the given show with force refresh`() = runTest(testDispatcher) {
        interactor.executeSync(SyncShowEpisodeWatchesInteractor.Param(showId = 1388L, forceRefresh = true))

        watchedEpisodeSyncRepository.getSyncedShowIds() shouldBe listOf(1388L)
        watchedEpisodeSyncRepository.wasForceRefreshUsed() shouldBe true
    }
}
