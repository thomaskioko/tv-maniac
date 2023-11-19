package com.thomaskioko.tvmaniac.presentation.showdetails

import com.thomaskioko.tvmaniac.core.db.ShowById
import com.thomaskioko.tvmaniac.core.db.SimilarShows
import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Season
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Show
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Trailer
import com.thomaskioko.tvmaniac.core.db.SeasonsByShowId as SeasonCache

fun List<SimilarShows>?.toSimilarShowList(): List<Show> = this?.map {
    Show(
        traktId = it.id.id,
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

fun ShowById?.toTvShow(): Show = this?.let {
    Show(
        traktId = it.id.id,
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
        isFollowed = it.in_watchlist == 1L,
    )
} ?: Show.EMPTY_SHOW

fun List<SeasonCache>?.toSeasonsList(): List<Season> = this?.map {
    Season(
        seasonId = it.season_id.id,
        tvShowId = it.show_id.id,
        name = it.season_title,
    )
} ?: emptyList()

fun List<Trailers>?.toTrailerList(): List<Trailer> = this?.map {
    Trailer(
        showId = it.show_id.id,
        key = it.key,
        name = it.name,
        youtubeThumbnailUrl = "https://i.ytimg.com/vi/${it.key}/hqdefault.jpg",
    )
} ?: emptyList()
