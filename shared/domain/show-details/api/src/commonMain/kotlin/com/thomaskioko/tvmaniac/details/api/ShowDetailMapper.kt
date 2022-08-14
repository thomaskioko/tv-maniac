package com.thomaskioko.tvmaniac.details.api

import com.thomaskioko.tvmaniac.datasource.cache.SelectSeasonsByShowId
import com.thomaskioko.tvmaniac.seasons.api.model.SeasonUiModel
import com.thomaskioko.tvmaniac.datasource.cache.SelectSimilarShows
import com.thomaskioko.tvmaniac.showcommon.api.model.TvShow
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.datasource.cache.Genre
import com.thomaskioko.tvmaniac.datasource.cache.Show
import com.thomaskioko.tvmaniac.genre.api.GenreUIModel
import com.thomaskioko.tvmaniac.lastairepisodes.api.LastAirEpisode
import com.thomaskioko.tvmaniac.datasource.cache.AirEpisodesByShowId
import com.thomaskioko.tvmaniac.datasource.cache.Trailers
import com.thomaskioko.tvmaniac.shared.domain.trailers.api.model.Trailer

fun Resource<List<SelectSimilarShows>>.toSimilarShowList(): List<TvShow> = data?.map {
    TvShow(
        id = it.id,
        title = it.title,
        overview = it.description,
        language = it.language,
        posterImageUrl = it.poster_image_url,
        backdropImageUrl = it.backdrop_image_url,
        votes = it.votes.toInt(),
        averageVotes = it.vote_average,
        genreIds = it.genre_ids,
        year = it.year,
        status = it.status,
        following = it.following
    )
} ?: emptyList()


fun Resource<Show>.toTvShow(): TvShow = data?.let {
    TvShow(
        id = it.id,
        title = it.title,
        overview = it.description,
        language = it.language,
        posterImageUrl = it.poster_image_url,
        backdropImageUrl = it.backdrop_image_url,
        votes = it.votes.toInt(),
        averageVotes = it.vote_average,
        genreIds = it.genre_ids,
        year = it.year,
        status = it.status,
        following = it.following
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
        tvShowId = it.tv_show_id,
        name = it.name,
        overview = it.overview,
        seasonNumber = it.season_number,
        episodeCount = it.epiosode_count.toInt()
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
        showId = it.show_id,
        key = it.key,
        name = it.name,
        youtubeThumbnailUrl = "https://i.ytimg.com/vi/${it.key}/hqdefault.jpg"
    )
} ?: emptyList()