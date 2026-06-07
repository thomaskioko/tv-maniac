package com.thomaskioko.tvmaniac.seasondetails.implementation

import com.thomaskioko.tvmaniac.db.Episode
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Season
import com.thomaskioko.tvmaniac.db.ShowId
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbSeasonDetailsResponse
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import com.thomaskioko.tvmaniac.util.api.FormatterUtil
import dev.zacsweers.metro.Inject

@Inject
public class TmdbSeasonMapper(
    private val formatterUtil: FormatterUtil,
    private val dateTimeProvider: DateTimeProvider,
) {

    public fun mapToSeason(response: TmdbSeasonDetailsResponse, showId: Id<ShowId>): Season =
        Season(
            id = Id(response.id.toLong()),
            show_id = showId,
            season_number = response.seasonNumber.toLong(),
            episode_count = response.episodes.size.toLong(),
            title = response.name,
            overview = response.overview,
            image_url = response.posterPath?.let { formatterUtil.formatTmdbPosterPath(it) },
        )

    public fun mapToEpisodes(response: TmdbSeasonDetailsResponse, showId: Id<ShowId>): List<Episode> =
        response.episodes.map { episode ->
            Episode(
                id = Id(episode.id.toLong()),
                season_id = Id(response.id.toLong()),
                show_id = showId,
                episode_number = episode.episodeNumber.toLong(),
                title = episode.name,
                overview = episode.overview,
                runtime = episode.runtime?.toLong() ?: 0L,
                vote_count = episode.voteCount.toLong(),
                ratings = episode.voteAverage,
                image_url = episode.stillPath?.let { formatterUtil.formatTmdbPosterPath(it) },
                trakt_id = null,
                first_aired = dateTimeProvider.isoDateToEpoch(episode.airDate),
            )
        }
}
