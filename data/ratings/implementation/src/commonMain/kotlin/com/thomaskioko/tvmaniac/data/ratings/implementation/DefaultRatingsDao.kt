package com.thomaskioko.tvmaniac.data.ratings.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.ratings.api.EpisodeRatingEntry
import com.thomaskioko.tvmaniac.data.ratings.api.RatingsDao
import com.thomaskioko.tvmaniac.data.ratings.api.SeasonRatingEntry
import com.thomaskioko.tvmaniac.data.ratings.api.ShowRatingEntry
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultRatingsDao(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : RatingsDao {

    private val queries = database.ratingsQueries

    override fun observePendingRatingsCount(): Flow<Long> = queries.pendingRatingsCount()
        .asFlow()
        .mapToOne(dispatchers.io)

    override fun upsertShowUserRating(showId: Long, userRating: Long, ratedAt: Long, pendingAction: PendingAction) {
        queries.upsertShowUserRating(
            showId = Id(showId),
            userRating = userRating,
            ratedAt = ratedAt,
            pendingAction = pendingAction.value,
        )
    }

    override fun observeShowRating(showId: Long): Flow<ShowRatingEntry?> = queries.observeShowRating(Id(showId))
        .asFlow()
        .mapToOneOrNull(dispatchers.io)
        .map { row ->
            row?.let {
                ShowRatingEntry(
                    showId = it.show_id.id,
                    userRating = it.user_rating,
                    ratedAt = it.rated_at,
                    pendingAction = PendingAction.fromValue(it.pending_action),
                )
            }
        }

    override fun clearShowUserRating(showId: Long) {
        queries.clearShowUserRating(Id(showId))
    }

    override fun showRatingsWithUploadPendingAction(): List<ShowRatingEntry> =
        queries.showRatingsWithUploadPendingAction()
            .executeAsList()
            .map {
                ShowRatingEntry(
                    showId = it.show_id.id,
                    userRating = it.user_rating,
                    ratedAt = it.rated_at,
                    pendingAction = PendingAction.fromValue(it.pending_action),
                )
            }

    override fun showRatingsWithDeletePendingAction(): List<ShowRatingEntry> =
        queries.showRatingsWithDeletePendingAction()
            .executeAsList()
            .map {
                ShowRatingEntry(
                    showId = it.show_id.id,
                    userRating = it.user_rating,
                    ratedAt = it.rated_at,
                    pendingAction = PendingAction.fromValue(it.pending_action),
                )
            }

    override fun updateShowRatingPendingAction(showId: Long, action: PendingAction) {
        queries.updateShowRatingPendingAction(pendingAction = action.value, showId = Id(showId))
    }

    override fun deleteShowRating(showId: Long) {
        queries.deleteShowRating(Id(showId))
    }

    override fun upsertSeasonUserRating(seasonId: Long, userRating: Long, ratedAt: Long, pendingAction: PendingAction) {
        queries.upsertSeasonUserRating(
            seasonId = Id(seasonId),
            userRating = userRating,
            ratedAt = ratedAt,
            pendingAction = pendingAction.value,
        )
    }

    override fun observeSeasonRating(seasonId: Long): Flow<SeasonRatingEntry?> = queries.observeSeasonRating(Id(seasonId))
        .asFlow()
        .mapToOneOrNull(dispatchers.io)
        .map { row ->
            row?.let {
                SeasonRatingEntry(
                    seasonId = it.season_id.id,
                    userRating = it.user_rating,
                    ratedAt = it.rated_at,
                    pendingAction = PendingAction.fromValue(it.pending_action),
                )
            }
        }

    override fun clearSeasonUserRating(seasonId: Long) {
        queries.clearSeasonUserRating(Id(seasonId))
    }

    override fun seasonRatingsWithUploadPendingAction(): List<SeasonRatingEntry> =
        queries.seasonRatingsWithUploadPendingAction()
            .executeAsList()
            .map {
                SeasonRatingEntry(
                    seasonId = it.season_id.id,
                    userRating = it.user_rating,
                    ratedAt = it.rated_at,
                    pendingAction = PendingAction.fromValue(it.pending_action),
                )
            }

    override fun seasonRatingsWithDeletePendingAction(): List<SeasonRatingEntry> =
        queries.seasonRatingsWithDeletePendingAction()
            .executeAsList()
            .map {
                SeasonRatingEntry(
                    seasonId = it.season_id.id,
                    userRating = it.user_rating,
                    ratedAt = it.rated_at,
                    pendingAction = PendingAction.fromValue(it.pending_action),
                )
            }

    override fun updateSeasonRatingPendingAction(seasonId: Long, action: PendingAction) {
        queries.updateSeasonRatingPendingAction(pendingAction = action.value, seasonId = Id(seasonId))
    }

    override fun deleteSeasonRating(seasonId: Long) {
        queries.deleteSeasonRating(Id(seasonId))
    }

    override fun upsertEpisodeUserRating(episodeId: Long, userRating: Long, ratedAt: Long, pendingAction: PendingAction) {
        queries.upsertEpisodeUserRating(
            episodeId = Id(episodeId),
            userRating = userRating,
            ratedAt = ratedAt,
            pendingAction = pendingAction.value,
        )
    }

    override fun observeEpisodeRating(episodeId: Long): Flow<EpisodeRatingEntry?> = queries.observeEpisodeRating(Id(episodeId))
        .asFlow()
        .mapToOneOrNull(dispatchers.io)
        .map { row ->
            row?.let {
                EpisodeRatingEntry(
                    episodeId = it.episode_id.id,
                    userRating = it.user_rating,
                    ratedAt = it.rated_at,
                    pendingAction = PendingAction.fromValue(it.pending_action),
                )
            }
        }

    override fun clearEpisodeUserRating(episodeId: Long) {
        queries.clearEpisodeUserRating(Id(episodeId))
    }

    override fun episodeRatingsWithUploadPendingAction(): List<EpisodeRatingEntry> =
        queries.episodeRatingsWithUploadPendingAction()
            .executeAsList()
            .map {
                EpisodeRatingEntry(
                    episodeId = it.episode_id.id,
                    userRating = it.user_rating,
                    ratedAt = it.rated_at,
                    pendingAction = PendingAction.fromValue(it.pending_action),
                )
            }

    override fun episodeRatingsWithDeletePendingAction(): List<EpisodeRatingEntry> =
        queries.episodeRatingsWithDeletePendingAction()
            .executeAsList()
            .map {
                EpisodeRatingEntry(
                    episodeId = it.episode_id.id,
                    userRating = it.user_rating,
                    ratedAt = it.rated_at,
                    pendingAction = PendingAction.fromValue(it.pending_action),
                )
            }

    override fun updateEpisodeRatingPendingAction(episodeId: Long, action: PendingAction) {
        queries.updateEpisodeRatingPendingAction(pendingAction = action.value, episodeId = Id(episodeId))
    }

    override fun deleteEpisodeRating(episodeId: Long) {
        queries.deleteEpisodeRating(Id(episodeId))
    }

    override fun clearAll() {
        queries.transaction {
            queries.deleteAllShowRatings()
            queries.deleteAllSeasonRatings()
            queries.deleteAllEpisodeRatings()
        }
    }
}
