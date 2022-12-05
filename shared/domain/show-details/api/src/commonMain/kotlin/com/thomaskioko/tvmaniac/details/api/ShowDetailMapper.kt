package com.thomaskioko.tvmaniac.details.api

import com.thomaskioko.tvmaniac.core.db.SelectByShowId
import com.thomaskioko.tvmaniac.core.db.SelectSeasonsByShowId
import com.thomaskioko.tvmaniac.core.db.SelectSimilarShows
import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.details.api.model.Season
import com.thomaskioko.tvmaniac.details.api.model.Show
import com.thomaskioko.tvmaniac.details.api.model.Trailer

fun Resource<List<SelectSimilarShows>>.toSimilarShowList(): List<Show> = data?.map {
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


fun Resource<SelectByShowId>.toTvShow(): Show = data?.let {
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

fun Resource<List<SelectSeasonsByShowId>>.toSeasonsEntityList(): List<Season> = data?.map {
    Season(
        seasonId = it.id,
        tvShowId = it.show_id,
        name = it.name,
        overview = it.overview,
        seasonNumber = it.season_number,
        episodeCount = it.epiosode_count
    )
} ?: emptyList()

fun Resource<List<Trailers>>.toTrailerList(): List<Trailer> = data?.map {
    Trailer(
        showId = it.trakt_id,
        key = it.key,
        name = it.name,
        youtubeThumbnailUrl = "https://i.ytimg.com/vi/${it.key}/hqdefault.jpg"
    )
} ?: emptyList()