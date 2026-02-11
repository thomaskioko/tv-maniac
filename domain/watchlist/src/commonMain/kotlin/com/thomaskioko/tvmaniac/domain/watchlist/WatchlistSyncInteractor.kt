package com.thomaskioko.tvmaniac.domain.watchlist

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.library.LibraryRepository
import com.thomaskioko.tvmaniac.syncactivity.api.TraktActivityRepository
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
public class WatchlistSyncInteractor(
    private val libraryRepository: LibraryRepository,
    private val traktActivityRepository: TraktActivityRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<WatchlistSyncInteractor.Param>() {

    override suspend fun doWork(params: Param) {
        withContext(dispatchers.io) {
            traktActivityRepository.fetchLatestActivities(params.forceRefresh)

            libraryRepository.syncLibrary(params.forceRefresh)
        }
    }

    public data class Param(
        val forceRefresh: Boolean = false,
    )

    private companion object {
        private const val TAG = "FollowedShowsSyncInteractor"
    }
}
