package com.thomaskioko.tvmaniac.domain.accountswitcher

import com.thomaskioko.tvmaniac.data.library.testing.FakeLibraryRepository
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.lists.testing.FakeListRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class CountUnsavedChangesTest {

    private val libraryRepository = FakeLibraryRepository()
    private val watchedEpisodeSyncRepository = FakeWatchedEpisodeSyncRepository()
    private val listRepository = FakeListRepository()

    private val countUnsavedChanges = CountUnsavedChanges(
        libraryRepository = libraryRepository,
        watchedEpisodeSyncRepository = watchedEpisodeSyncRepository,
        listRepository = listRepository,
    )

    @Test
    fun `should sum pending counts across all repositories given pending changes`() = runTest {
        libraryRepository.setPendingFollowedShowsCount(2L)
        watchedEpisodeSyncRepository.setPendingEpisodesCount(3L)
        listRepository.setPendingListShowsCount(1L)

        countUnsavedChanges() shouldBe 6
    }

    @Test
    fun `should return zero given no pending changes`() = runTest {
        countUnsavedChanges() shouldBe 0
    }
}
