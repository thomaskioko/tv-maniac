package com.thomaskioko.tvmaniac.domain.accountswitcher

import com.thomaskioko.tvmaniac.data.library.LibraryRepository
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.lists.api.ListRepository
import dev.zacsweers.metro.Inject

@Inject
public class CountUnsavedChanges(
    private val libraryRepository: LibraryRepository,
    private val watchedEpisodeSyncRepository: WatchedEpisodeSyncRepository,
    private val listRepository: ListRepository,
) {

    public suspend operator fun invoke(): Int = getPendingCount().toInt()

    private suspend fun getPendingCount(): Long = libraryRepository.countPendingFollowedShows() +
        watchedEpisodeSyncRepository.countPendingEpisodes() +
        listRepository.countPendingListShows() +
        listRepository.countPendingLists()
}
