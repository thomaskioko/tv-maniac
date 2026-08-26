package com.thomaskioko.tvmaniac.domain.widget

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.upnext.api.UpNextRepository
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Inject
public class ObserveWidgetShowsInteractor(
    private val upNextRepository: UpNextRepository,
) : SubjectInteractor<Unit, List<WidgetShow>>() {

    override fun createObservable(params: Unit): Flow<List<WidgetShow>> =
        upNextRepository.observeNextEpisodesForWatchlist()
            .map { episodes ->
                episodes
                    .filterNot { it.isCompleted() }
                    .mapNotNull { it.toWidgetShow() }
                    .take(MAX_ENTRIES)
            }

    public companion object {
        public const val MAX_ENTRIES: Int = 6
    }
}

private fun NextEpisodeWithShow.isCompleted(): Boolean = totalCount in 1..watchedCount

private fun NextEpisodeWithShow.toWidgetShow(): WidgetShow? {
    val resolvedShowName = showName ?: return null
    val resolvedSeasonNumber = seasonNumber ?: return null
    val resolvedEpisodeNumber = episodeNumber ?: return null
    return WidgetShow(
        tmdbId = showId,
        showName = resolvedShowName,
        episodeName = episodeName ?: "",
        seasonNumber = resolvedSeasonNumber,
        episodeNumber = resolvedEpisodeNumber,
        posterUrl = showPoster,
    )
}
