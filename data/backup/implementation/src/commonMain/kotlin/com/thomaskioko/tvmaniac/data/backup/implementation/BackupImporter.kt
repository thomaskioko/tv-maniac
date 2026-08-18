package com.thomaskioko.tvmaniac.data.backup.implementation

import com.thomaskioko.tvmaniac.data.backup.api.BackupFile
import com.thomaskioko.tvmaniac.data.backup.api.BackupShow
import com.thomaskioko.tvmaniac.data.backup.api.RestoreSummary
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
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

    fun import(backup: BackupFile, includeSpecials: Boolean): RestoreSummary = transactionRunner {
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
            episodeCount += restoreShow(show, showId)
            skippedSeasonRatings += restoreSeasonRatings(show, showId)
            skippedEpisodeRatings += restoreEpisodeRatings(show, showId)
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
            overview = "",
            language = null,
            year = null,
            ratings = 0.0,
            vote_count = 0,
            genres = null,
            status = null,
            episode_numbers = null,
            season_numbers = null,
            poster_path = null,
            backdrop_path = null,
        )
        return database.tvShowQueries.getShowIdByTmdbId(tmdbId).executeAsOneOrNull()
    }

    private fun restoreShow(show: BackupShow, showId: Id<ShowId>): Int {
        show.followedAt?.let { followedAt ->
            restoreQueries.restoreFollowedShow(
                showId = showId,
                tmdbId = Id(show.tmdbId),
                followedAt = followedAt,
            )
        }

        watchStatusOrNull(show.watchStatus)?.let { status ->
            restoreQueries.restoreWatchStatus(
                showId = showId,
                status = status,
                lastWatchedAt = show.watchedEpisodes.maxOfOrNull { it.watchedAt },
            )
        }

        show.rating?.takeIf { it.value in RATING_RANGE }?.let { rating ->
            restoreQueries.restoreShowRating(
                showId = showId,
                userRating = rating.value,
                ratedAt = rating.ratedAt,
            )
        }

        show.watchedEpisodes.forEach { episode ->
            restoreQueries.restoreWatchedEpisode(
                showId = showId,
                seasonNumber = episode.season,
                episodeNumber = episode.episode,
                watchedAt = episode.watchedAt,
            )
        }

        return show.watchedEpisodes.size
    }

    private fun restoreSeasonRatings(show: BackupShow, showId: Id<ShowId>): Int {
        var skipped = 0
        show.seasonRatings.forEach { rating ->
            val seasonId = restoreQueries.seasonIdForNumber(showId, rating.season).executeAsOneOrNull()
            if (seasonId == null || rating.value !in RATING_RANGE) {
                skipped++
                return@forEach
            }
            restoreQueries.restoreSeasonRating(
                seasonId = seasonId,
                userRating = rating.value,
                ratedAt = rating.ratedAt,
            )
        }
        return skipped
    }

    private fun restoreEpisodeRatings(show: BackupShow, showId: Id<ShowId>): Int {
        var skipped = 0
        show.episodeRatings.forEach { rating ->
            val episodeId = restoreQueries
                .episodeIdForNumber(showId, rating.season, rating.episode)
                .executeAsOneOrNull()
            if (episodeId == null || rating.value !in RATING_RANGE) {
                skipped++
                return@forEach
            }
            restoreQueries.restoreEpisodeRating(
                episodeId = episodeId,
                userRating = rating.value,
                ratedAt = rating.ratedAt,
            )
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
