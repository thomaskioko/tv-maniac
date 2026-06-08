package com.thomaskioko.tvmaniac.domain.continuewatching

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.domain.continuewatching.model.WatchlistSections
import com.thomaskioko.tvmaniac.domain.continuewatching.model.WatchlistShowInfo
import com.thomaskioko.tvmaniac.upnext.api.UpNextRepository
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val THREE_WEEKS_MILLIS = 21 * 24 * 60 * 60 * 1000L

@Inject
public class ObserveWatchlistSectionsInteractor(
    private val upNextRepository: UpNextRepository,
    private val dateTimeProvider: DateTimeProvider,
) : SubjectInteractor<String, WatchlistSections>() {

    override fun createObservable(params: String): Flow<WatchlistSections> {
        return upNextRepository.observeNextEpisodesForWatchlist()
            .map { episodes ->
                episodes
                    .filter { params.isBlank() || it.showName?.contains(params, ignoreCase = true) == true }
                    .map { it.toWatchlistShowInfo() }
                    .filterNot { it.isCompleted() }
                    .groupBySections(dateTimeProvider.nowMillis())
            }
    }
}

private fun List<WatchlistShowInfo>.groupBySections(currentTimeMillis: Long): WatchlistSections {
    val threeWeeksAgo = currentTimeMillis - THREE_WEEKS_MILLIS

    val watchNext = mutableListOf<WatchlistShowInfo>()
    val stale = mutableListOf<WatchlistShowInfo>()

    forEach { item ->
        val lastWatched = item.lastWatchedAt ?: 0L
        val isStale = lastWatched in 1 until threeWeeksAgo

        if (isStale) stale.add(item) else watchNext.add(item)
    }

    return WatchlistSections(
        watchNext = watchNext,
        stale = stale,
    )
}
