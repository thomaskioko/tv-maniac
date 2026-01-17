package com.thomaskioko.tvmaniac.domain.showdetails

import com.thomaskioko.tvmaniac.db.SelectByShowTraktId
import com.thomaskioko.tvmaniac.db.ShowCast
import com.thomaskioko.tvmaniac.db.ShowSeasons
import com.thomaskioko.tvmaniac.db.SimilarShows
import com.thomaskioko.tvmaniac.db.WatchProviders
import com.thomaskioko.tvmaniac.domain.showdetails.model.Casts
import com.thomaskioko.tvmaniac.domain.showdetails.model.Providers
import com.thomaskioko.tvmaniac.domain.showdetails.model.Season
import com.thomaskioko.tvmaniac.domain.showdetails.model.Show
import com.thomaskioko.tvmaniac.domain.showdetails.model.Trailer
import com.thomaskioko.tvmaniac.episodes.api.model.SeasonWatchProgress

internal fun List<ShowCast>.toCastList(): List<Casts> =
    map {
        Casts(
            id = it.cast_id.id,
            name = it.name,
            profileUrl = it.profile_path,
            characterName = it.character_name,
        )
    }

internal fun List<SimilarShows>.toSimilarShowList(): List<Show> =
    map {
        Show(
            traktId = it.show_trakt_id.id,
            title = it.name,
            posterImageUrl = it.poster_path,
            backdropImageUrl = it.backdrop_path,
            isInLibrary = it.in_library == 1L,
        )
    }

internal fun List<WatchProviders>.toWatchProviderList(): List<Providers> =
    map {
        Providers(
            id = it.provider_id.id,
            name = it.name ?: "",
            logoUrl = it.logo_path,
        )
    }

internal fun List<ShowSeasons>.toSeasonsList(
    progressMap: Map<Long, SeasonWatchProgress> = emptyMap(),
): List<Season> =
    map { season ->
        val progress = progressMap[season.season_number]
        Season(
            seasonId = season.season_id.id,
            tvShowId = season.show_trakt_id.id,
            name = season.season_title,
            seasonNumber = season.season_number,
            watchedCount = progress?.watchedCount ?: 0,
            totalCount = progress?.totalCount ?: 0,
        )
    }

internal fun List<SelectByShowTraktId>.toTrailerList(): List<Trailer> =
    map { trailer ->
        Trailer(
            showTmdbId = trailer.show_tmdb_id.id,
            key = trailer.trailer_id,
            name = trailer.name,
            youtubeThumbnailUrl = "https://i.ytimg.com/vi/${trailer.trailer_id}/hqdefault.jpg",
        )
    }
