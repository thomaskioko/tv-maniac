package com.thomaskioko.tvmaniac.domain.accountswitcher

import com.thomaskioko.tvmaniac.core.base.interactor.executeSync
import com.thomaskioko.tvmaniac.data.library.testing.FakeLibraryRepository
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class PushPendingChangesInteractorTest {

    private val watchedEpisodeSyncRepository = FakeWatchedEpisodeSyncRepository()
    private val libraryRepository = FakeLibraryRepository()

    private val interactor = PushPendingChangesInteractor(
        watchedEpisodeSyncRepository = watchedEpisodeSyncRepository,
        libraryRepository = libraryRepository,
    )

    @Test
    fun `should push pending episodes and followed shows given invoked`() = runTest {
        interactor.executeSync()

        watchedEpisodeSyncRepository.syncPendingCallCount() shouldBe 1
        libraryRepository.syncPendingFollowedShowsInvocations() shouldBe 1
    }
}
