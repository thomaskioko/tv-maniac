package com.thomaskioko.tvmaniac.details.api

import com.thomaskioko.tvmaniac.core.db.AirEpisodesByShowId
import com.thomaskioko.tvmaniac.core.db.Genre
import com.thomaskioko.tvmaniac.core.db.SelectSeasonsByShowId
import com.thomaskioko.tvmaniac.core.db.SelectSimilarShows
import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.seasons.api.model.SeasonUiModel
import com.thomaskioko.tvmaniac.showcommon.api.model.TvShow
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.genre.api.GenreUIModel
import com.thomaskioko.tvmaniac.lastairepisodes.api.LastAirEpisode
import com.thomaskioko.tvmaniac.shared.domain.trailers.api.model.Trailer

fun Resource<List<SelectSimilarShows>>.toSimilarShowList(): List<TvShow> = data?.map {
    TvShow(
        traktId = it.trakt_id_,
        tmdbId = it.tmdb_id,
        title = it.title,
        overview = it.overview,
        language = it.language,
        posterImageUrl = it.poster_image_url,
        backdropImageUrl = it.backdrop_image_url,
        votes = it.votes,
        rating = it.rating,
        genres = it.genres,
        year = it.year,
        status = it.status,
    )
} ?: emptyList()


fun Resource<Show>.toTvShow(): TvShow = data?.let {
    TvShow(
        traktId = it.trakt_id,
        tmdbId = it.tmdb_id,
        title = it.title,
        overview = it.overview,
        language = it.language,
        posterImageUrl = it.poster_image_url,
        backdropImageUrl = it.backdrop_image_url,
        votes = it.votes,
        rating = it.rating,
        genres = it.genres,
        year = it.year,
        status = it.status,
    )
} ?: TvShow.EMPTY_SHOW

fun Resource<List<Genre>>.toGenreModelList(genreIds: List<Int>): List<GenreUIModel> =
    data?.filter { genre ->
        genreIds.any { id -> genre.id == id.toLong() }
    }?.map {
        GenreUIModel(
            id = it.id.toInt(),
            name = it.name
        )
    } ?: emptyList()


fun Resource<List<SelectSeasonsByShowId>>.toSeasonsEntityList(): List<SeasonUiModel> = data?.map {
    SeasonUiModel(
        seasonId = it.id,
        tvShowId = it.trakt_id,
        name = it.name,
        overview = it.overview,
        seasonNumber = it.season_number,
        episodeCount = it.epiosode_count
    )
} ?: emptyList()


fun List<AirEpisodesByShowId>.toLastAirEpisodeList(): List<LastAirEpisode> = map {
    LastAirEpisode(
        id = it.id,
        name = "S${it.season_number}.E${
            it.episode_number.toString()
                .padStart(2, '0')
        } • ${it.name}",
        overview = it.overview,
        airDate = it.air_date,
        episodeNumber = it.episode_number,
        seasonNumber = it.season_number,
        posterPath = it.still_path,
        voteAverage = it.vote_average,
        voteCount = it.vote_count,
        title = it.title
    )
}

fun Resource<List<Trailers>>.toTrailerList(): List<Trailer> = data?.map {
    Trailer(
        showId = it.trakt_id,
        key = it.key,
        name = it.name,
        youtubeThumbnailUrl = "https://i.ytimg.com/vi/${it.key}/hqdefault.jpg"
    )
} ?: emptyList()