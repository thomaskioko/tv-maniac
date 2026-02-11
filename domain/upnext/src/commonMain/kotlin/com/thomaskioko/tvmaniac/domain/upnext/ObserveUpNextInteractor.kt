package com.thomaskioko.tvmaniac.domain.upnext

import com.thomaskioko.tvmaniac.domain.upnext.model.UpNextResult
import com.thomaskioko.tvmaniac.domain.upnext.model.UpNextSortOption
import com.thomaskioko.tvmaniac.upnext.api.UpNextRepository
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

@Inject
public class ObserveUpNextInteractor(
    private val repository: UpNextRepository,
    private val dateTimeProvider: DateTimeProvider,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    public val flow: Flow<UpNextResult> = repository.observeUpNextSortOption()
        .map { it.toUpNextSortOption() }
        .distinctUntilChanged()
        .flatMapLatest { sortOption ->
            repository.observeNextEpisodesForWatchlist()
                .map { episodes ->
                    UpNextResult(
                        sortOption = sortOption,
                        episodes = episodes
                            .filterAired(dateTimeProvider.nowMillis(), sortOption),
                    )
                }
        }
}

private fun List<NextEpisodeWithShow>.filterAired(nowMillis: Long, option: UpNextSortOption): List<NextEpisodeWithShow> =
    filter { episode ->
        val airDate = episode.firstAired
        airDate == null || airDate <= nowMillis
    }.sortedBy(option)

private fun List<NextEpisodeWithShow>.sortedBy(option: UpNextSortOption): List<NextEpisodeWithShow> =
    when (option) {
        UpNextSortOption.LAST_WATCHED -> sortedBy { it.lastWatchedAt ?: it.followedAt ?: Long.MAX_VALUE }
        UpNextSortOption.AIR_DATE -> sortedBy { it.firstAired ?: Long.MAX_VALUE }
    }

private fun String.toUpNextSortOption(): UpNextSortOption = when (this) {
    UpNextSortOption.AIR_DATE.name -> UpNextSortOption.AIR_DATE
    else -> UpNextSortOption.LAST_WATCHED
}
