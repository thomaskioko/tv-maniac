package com.thomaskioko.tvmaniac.shows.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Provider
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.shows.api.ShowToPersist
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.shows.api.mergeShows
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.withContext

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTvShowsDao(
    database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : TvShowsDao {

    private val tvShowQueries = database.tvShowQueries
    private val externalIdQueries = database.tvshowExternalIdQueries

    override fun upsert(show: ShowToPersist) {
        tvShowQueries.transaction {
            upsertShowWithGenres(show)
        }
    }

    override fun upsert(list: List<ShowToPersist>) {
        if (list.isEmpty()) return

        tvShowQueries.transaction {
            list.forEach { show ->
                upsertShowWithGenres(show)
            }
        }
    }

    override fun upsertExternalId(tmdbId: Long, provider: Provider, externalId: String) {
        val showId = tvShowQueries.getShowIdByTmdbId(Id(tmdbId)).executeAsOneOrNull() ?: return
        externalIdQueries.insert(
            showId = showId,
            provider = provider,
            externalId = externalId,
        )
    }

    private fun upsertShowWithGenres(show: ShowToPersist) {
        tvShowQueries.upsert(
            tmdb_id = show.tmdbId,
            name = show.name,
            overview = show.overview,
            language = show.language,
            year = show.year,
            ratings = show.ratings,
            vote_count = show.voteCount,
            genres = show.genres,
            status = show.status,
            episode_numbers = show.episodeNumbers,
            season_numbers = show.seasonNumbers,
            poster_path = show.posterPath,
            backdrop_path = show.backdropPath,
        )
        val showId = tvShowQueries.getShowIdByTmdbId(show.tmdbId).executeAsOne()
        show.showId?.let { traktId ->
            externalIdQueries.insert(
                showId = showId,
                provider = Provider.TRAKT,
                externalId = traktId.id.toString(),
            )
        }
    }

    override fun getTmdbIdsWithPoster(tmdbIds: List<Long>): Set<Long> {
        if (tmdbIds.isEmpty()) return emptySet()

        return tvShowQueries.tmdbIdsWithPoster(tmdbIds.map { Id<TmdbId>(it) })
            .executeAsList()
            .map { it.id }
            .toSet()
    }

    override fun deleteTvShows() {
        tvShowQueries.transaction { tvShowQueries.deleteAll() }
    }

    override fun upsertMerging(show: ShowToPersist) {
        tvShowQueries.transaction {
            val existing = tvShowQueries.tvshowByTmdbId(show.tmdbId).executeAsOneOrNull()
            upsertShowWithGenres(mergeShows(existing, show))
        }
    }

    override fun getShowsByIds(showIds: List<Long>): List<ShowEntity> {
        if (showIds.isEmpty()) return emptyList()

        return tvShowQueries.showsByTmdbIds(showIds.map { Id(it) }) { traktId, tmdbId, name, posterPath, overview, inLibrary ->
            ShowEntity(
                showId = traktId.id,
                tmdbId = tmdbId.id,
                title = name,
                posterPath = posterPath,
                overview = overview,
                inLibrary = inLibrary == 1L,
            )
        }.executeAsList()
    }

    override fun getTmdbIdByShowId(showId: Long): Long? {
        return tvShowQueries.getTmdbIdByShowId(showId).executeAsOneOrNull()?.id
    }

    override fun getTmdbIdForLocalShowId(showId: Long): Long? {
        return tvShowQueries.tmdbIdForLocalShowId(Id(showId)).executeAsOneOrNull()?.id
    }

    override fun getLocalShowIdByTmdbId(tmdbId: Long): Long? {
        return tvShowQueries.getShowIdByTmdbId(Id(tmdbId)).executeAsOneOrNull()?.id
    }

    override fun getTraktIdByTmdbId(tmdbId: Long): Long? =
        tvShowQueries.getTraktIdByTmdbId(Id(tmdbId)).executeAsOneOrNull()

    override suspend fun existsByShowId(showId: Long): Boolean =
        withContext(dispatchers.io) {
            tvShowQueries.existsByShowId(showId).executeAsOne()
        }
}
