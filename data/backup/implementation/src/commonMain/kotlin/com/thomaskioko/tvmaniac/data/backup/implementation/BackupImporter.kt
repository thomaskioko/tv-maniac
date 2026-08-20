package com.thomaskioko.tvmaniac.data.backup.implementation

import com.thomaskioko.tvmaniac.data.backup.api.BackupFile
import com.thomaskioko.tvmaniac.data.backup.api.BackupShow
import com.thomaskioko.tvmaniac.data.backup.api.RestoreSummary
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.EpisodeId
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.SeasonId
import com.thomaskioko.tvmaniac.db.ShowId
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.db.WatchStatus
import com.thomaskioko.tvmaniac.episodes.api.WatchedDate

internal class BackupImporter(
    private val database: TvManiacDatabase,
    private val transactionRunner: DatabaseTransactionRunner,
) {

    private val restoreQueries = database.restoreQueries

    fun import(
        backup: BackupFile,
        includeSpecials: Boolean,
        syncWithConnectedAccount: Boolean,
    ): RestoreSummary = transactionRunner {
        clearRestoredTables()

        var showCount = 0
        var episodeCount = 0
        var skippedSeasonRatings = 0
        var skippedEpisodeRatings = 0
        val skippedShows = mutableListOf<String>()

        backup.shows.forEach { show ->
            val showId = resolveShow(show)
            if (showId == null) {
                skippedShows += show.title
                return@forEach
            }

            showCount++
            restoreSeasonsAndEpisodes(show, showId)
            episodeCount += restoreShow(
                show = show,
                showId = showId,
                syncWithConnectedAccount = syncWithConnectedAccount,
            )
            skippedSeasonRatings += restoreSeasonRatings(show, showId, syncWithConnectedAccount)
            skippedEpisodeRatings += restoreEpisodeRatings(show, showId, syncWithConnectedAccount)
            recalculateMetadata(showId, includeSpecials)
        }

        database.continueWatchingQueries.insertMembershipFromWatchedEpisodes()

        RestoreSummary(
            showCount = showCount,
            episodeCount = episodeCount,
            skippedShows = skippedShows,
            skippedSeasonRatings = skippedSeasonRatings,
            skippedEpisodeRatings = skippedEpisodeRatings,
            rewatchSessionsKept = restoreQueries.rewatchSessionCount().executeAsOne().toInt(),
            listsNotRestored = backup.lists.size,
        )
    }

    private fun clearRestoredTables() {
        database.watchedEpisodesQueries.deleteAll()
        database.followedShowsQueries.deleteAll()
        database.showWatchStatusQueries.deleteAll()
        database.continueWatchingQueries.deleteAll()
        database.ratingsQueries.deleteAllShowRatings()
        database.ratingsQueries.deleteAllSeasonRatings()
        database.ratingsQueries.deleteAllEpisodeRatings()
    }

    private fun resolveShow(show: BackupShow): Id<ShowId>? {
        if (show.tmdbId <= 0) return null

        val tmdbId = Id<TmdbId>(show.tmdbId)
        database.tvShowQueries.getShowIdByTmdbId(tmdbId).executeAsOneOrNull()?.let { return it }

        database.tvShowQueries.upsert(
            tmdb_id = tmdbId,
            name = show.title,
            overview = show.overview.orEmpty(),
            language = show.language,
            year = show.year,
            ratings = show.ratings ?: 0.0,
            vote_count = show.voteCount ?: 0,
            genres = show.genres.takeIf { it.isNotEmpty() },
            status = show.status,
            episode_numbers = show.episodeNumbers,
            season_numbers = show.seasonNumbers,
            poster_path = show.posterPath,
            backdrop_path = show.backdropPath,
        )
        return database.tvShowQueries.getShowIdByTmdbId(tmdbId).executeAsOneOrNull()
    }

    private fun restoreSeasonsAndEpisodes(show: BackupShow, showId: Id<ShowId>) {
        show.seasons.forEach { season ->
            val seasonId = Id<SeasonId>(season.tmdbId)
            restoreQueries.restoreSeason(
                seasonId = seasonId,
                showId = showId,
                seasonNumber = season.seasonNumber,
                episodeCount = season.episodeCount,
                title = season.title,
                overview = season.overview,
                imageUrl = season.imageUrl,
            )
            season.episodes.forEach { episode ->
                restoreQueries.restoreEpisode(
                    episodeId = Id<EpisodeId>(episode.tmdbId),
                    seasonId = seasonId,
                    showId = showId,
                    episodeNumber = episode.episodeNumber,
                    title = episode.title,
                    overview = episode.overview.orEmpty(),
                    runtime = episode.runtime,
                    voteCount = episode.voteCount ?: 0,
                    ratings = episode.ratings ?: 0.0,
                    imageUrl = episode.imageUrl,
                    firstAired = episode.firstAired,
                )
            }
        }
    }

    private fun restoreShow(show: BackupShow, showId: Id<ShowId>, syncWithConnectedAccount: Boolean): Int {
        show.followedAt?.let { followedAt ->
            when {
                syncWithConnectedAccount -> restoreQueries.restoreFollowedShowForUpload(
                    showId = showId,
                    tmdbId = Id(show.tmdbId),
                    followedAt = followedAt,
                )
                else -> restoreQueries.restoreFollowedShow(
                    showId = showId,
                    tmdbId = Id(show.tmdbId),
                    followedAt = followedAt,
                )
            }
        }

        watchStatusOrNull(show.watchStatus)?.let { status ->
            restoreQueries.restoreWatchStatus(
                showId = showId,
                status = status,
                lastWatchedAt = show.watchedEpisodes.maxOfOrNull { it.watchedAt },
            )
        }

        show.rating?.takeIf { it.value in RATING_RANGE }?.let { rating ->
            when {
                syncWithConnectedAccount -> restoreQueries.restoreShowRatingForUpload(
                    showId = showId,
                    userRating = rating.value,
                    ratedAt = rating.ratedAt,
                )
                else -> restoreQueries.restoreShowRating(
                    showId = showId,
                    userRating = rating.value,
                    ratedAt = rating.ratedAt,
                )
            }
        }

        show.watchedEpisodes.forEach { episode ->
            when {
                syncWithConnectedAccount -> restoreQueries.restoreWatchedEpisodeForUpload(
                    showId = showId,
                    seasonNumber = episode.season,
                    episodeNumber = episode.episode,
                    watchedAt = episode.watchedAt,
                )
                else -> restoreQueries.restoreWatchedEpisode(
                    showId = showId,
                    seasonNumber = episode.season,
                    episodeNumber = episode.episode,
                    watchedAt = episode.watchedAt,
                )
            }
        }

        return show.watchedEpisodes.size
    }

    private fun restoreSeasonRatings(show: BackupShow, showId: Id<ShowId>, syncWithConnectedAccount: Boolean): Int {
        var skipped = 0
        show.seasonRatings.forEach { rating ->
            val seasonId = restoreQueries.seasonIdForNumber(showId, rating.season).executeAsOneOrNull()
            if (seasonId == null || rating.value !in RATING_RANGE) {
                skipped++
                return@forEach
            }
            when {
                syncWithConnectedAccount -> restoreQueries.restoreSeasonRatingForUpload(
                    seasonId = seasonId,
                    userRating = rating.value,
                    ratedAt = rating.ratedAt,
                )
                else -> restoreQueries.restoreSeasonRating(
                    seasonId = seasonId,
                    userRating = rating.value,
                    ratedAt = rating.ratedAt,
                )
            }
        }
        return skipped
    }

    private fun restoreEpisodeRatings(show: BackupShow, showId: Id<ShowId>, syncWithConnectedAccount: Boolean): Int {
        var skipped = 0
        show.episodeRatings.forEach { rating ->
            val episodeId = restoreQueries
                .episodeIdForNumber(showId, rating.season, rating.episode)
                .executeAsOneOrNull()
            if (episodeId == null || rating.value !in RATING_RANGE) {
                skipped++
                return@forEach
            }
            when {
                syncWithConnectedAccount -> restoreQueries.restoreEpisodeRatingForUpload(
                    episodeId = episodeId,
                    userRating = rating.value,
                    ratedAt = rating.ratedAt,
                )
                else -> restoreQueries.restoreEpisodeRating(
                    episodeId = episodeId,
                    userRating = rating.value,
                    ratedAt = rating.ratedAt,
                )
            }
        }
        return skipped
    }

    private fun recalculateMetadata(showId: Id<ShowId>, includeSpecials: Boolean) {
        database.showMetadataQueries.recalculateLastWatched(
            showId = showId,
            include_specials = if (includeSpecials) 1L else 0L,
            unknown_millis = WatchedDate.UNKNOWN_MILLIS,
        )
    }

    private fun watchStatusOrNull(name: String?): WatchStatus? =
        WatchStatus.entries.firstOrNull { it.name == name }

    private companion object {
        private val RATING_RANGE = 1L..10L
    }
}
