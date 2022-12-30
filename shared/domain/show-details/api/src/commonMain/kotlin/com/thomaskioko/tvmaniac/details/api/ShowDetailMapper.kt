package com.thomaskioko.tvmaniac.details.api

import com.thomaskioko.tvmaniac.core.db.SelectByShowId
import com.thomaskioko.tvmaniac.core.db.SelectSimilarShows
import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.details.api.model.Season
import com.thomaskioko.tvmaniac.details.api.model.Show
import com.thomaskioko.tvmaniac.details.api.model.Trailer
import com.thomaskioko.tvmaniac.core.db.Season as SeasonCache

fun List<SelectSimilarShows>?.toSimilarShowList(): List<Show> = this?.map {
    Show(
        traktId = it.trakt_id_,
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
        isFollowed = it.id != null && it.id == it.trakt_id
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
        youtubeThumbnailUrl = "https://i.ytimg.com/vi/${it.key}/hqdefault.jpg"
    )
} ?: emptyList()