package com.thomaskioko.tvmaniac.presentation.showdetails

import com.thomaskioko.tvmaniac.core.db.SelectByShowId
import com.thomaskioko.tvmaniac.core.db.SimilarShows
import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Season
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Show
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Trailer
import com.thomaskioko.tvmaniac.core.db.Seasons as SeasonCache

fun List<SimilarShows>?.toSimilarShowList(): List<Show> = this?.map {
    Show(
        traktId = it.trakt_id,
        tmdbId = it.tmdb_id,
        title = it.title,
        overview = it.overview,
        language = it.language,
        posterImageUrl = it.poster_url,
        backdropImageUrl = it.backdrop_url,
        votes = it.votes,
        rating = it.rating,
        genres = it.genres,
        year = it.year,
        status = it.status,
    )
} ?: emptyList()

fun SelectByShowId?.toTvShow(): Show = this?.let {
    Show(
        traktId = it.trakt_id,
        tmdbId = it.tmdb_id,
        title = it.title,
        overview = it.overview,
        language = it.language,
        posterImageUrl = it.poster_url,
        backdropImageUrl = it.backdrop_url,
        votes = it.votes,
        rating = it.rating,
        genres = it.genres,
        year = it.year,
        status = it.status,
        isFollowed = it.id != null && it.id == it.trakt_id,
        // TODO:: Get season count
    )
} ?: Show.EMPTY_SHOW

fun List<SeasonCache>?.toSeasonsList(): List<Season> = this?.map {
    Season(
        seasonId = it.id,
        tvShowId = it.show_trakt_id,
        name = it.name,
    )
} ?: emptyList()

fun List<Trailers>?.toTrailerList(): List<Trailer> = this?.map {
    Trailer(
        showId = it.trakt_id,
        key = it.key,
        name = it.name,
        youtubeThumbnailUrl = "https://i.ytimg.com/vi/${it.key}/hqdefault.jpg",
    )
} ?: emptyList()

fun Either<Failure, SelectByShowId?>.toShowState(): ShowState = fold(
    {
        ShowState.ShowError(it.errorMessage)
    },
    {
        ShowState.ShowLoaded(
            show = it.toTvShow(),
        )
    },
)

fun Either<Failure, List<com.thomaskioko.tvmaniac.core.db.Seasons>>.toSeasonState() = fold(
    {
        SeasonState.SeasonsError(it.errorMessage)
    },
    {
        SeasonState.SeasonsLoaded(
            isLoading = false,
            seasonsList = it.toSeasonsList(),
        )
    },
)
