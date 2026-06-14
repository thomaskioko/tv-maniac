package com.thomaskioko.tvmaniac.domain.accountswitcher

import com.thomaskioko.tvmaniac.data.library.LibraryRepository
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.traktlists.api.TraktListRepository
import dev.zacsweers.metro.Inject

@Inject
public class CountUnsavedChanges(
    private val libraryRepository: LibraryRepository,
    private val watchedEpisodeSyncRepository: WatchedEpisodeSyncRepository,
    private val traktListRepository: TraktListRepository,
) {

    public suspend operator fun invoke(): Int = getPendingCount().toInt()

    private suspend fun getPendingCount(): Long = libraryRepository.countPendingFollowedShows() +
        watchedEpisodeSyncRepository.countPendingEpisodes() +
        traktListRepository.countPendingListShows()
}
