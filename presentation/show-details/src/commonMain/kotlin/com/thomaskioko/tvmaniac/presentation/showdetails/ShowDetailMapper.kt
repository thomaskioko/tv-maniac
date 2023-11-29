package com.thomaskioko.tvmaniac.presentation.showdetails

import com.thomaskioko.tvmaniac.core.db.ShowById
import com.thomaskioko.tvmaniac.core.db.SimilarShows
import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Season
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Show
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Trailer
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import com.thomaskioko.tvmaniac.core.db.SeasonsByShowId as SeasonCache

fun List<SimilarShows>?.toSimilarShowList(): ImmutableList<Show> = this?.map {
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
        genres = it.genres.toPersistentList(),
        year = it.year,
        status = it.status,
    )
}?.toImmutableList() ?: persistentListOf()

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
        genres = it.genres.toPersistentList(),
        year = it.year,
        status = it.status,
        isFollowed = it.in_watchlist == 1L,
    )
} ?: Show.EMPTY_SHOW

fun List<SeasonCache>?.toSeasonsList(): ImmutableList<Season> = this?.map {
    Season(
        seasonId = it.season_id.id,
        tvShowId = it.show_id.id,
        name = it.season_title,
    )
}?.toImmutableList() ?: persistentListOf()

fun List<Trailers>?.toTrailerList(): ImmutableList<Trailer> = this?.map {
    Trailer(
        showId = it.show_id.id,
        key = it.key,
        name = it.name,
        youtubeThumbnailUrl = "https://i.ytimg.com/vi/${it.key}/hqdefault.jpg",
    )
}?.toImmutableList() ?: persistentListOf()
