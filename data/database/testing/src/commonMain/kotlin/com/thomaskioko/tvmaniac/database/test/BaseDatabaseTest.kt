package com.thomaskioko.tvmaniac.database.test

import app.cash.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.db.DatabaseFactory
import com.thomaskioko.tvmaniac.db.DefaultShowIdResolver
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Provider
import com.thomaskioko.tvmaniac.db.ShowId
import com.thomaskioko.tvmaniac.db.ShowIdResolver
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import kotlin.uuid.Uuid

internal expect fun createTestSqlDriver(name: String): SqlDriver

public abstract class BaseDatabaseTest {

    private val sqlDriver: SqlDriver by lazy {
        createTestSqlDriver("${this@BaseDatabaseTest::class.simpleName}_${Uuid.random()}")
    }

    protected val database: TvManiacDatabase by lazy { DatabaseFactory(sqlDriver).createDatabase() }

    protected val showIdResolver: ShowIdResolver by lazy { DefaultShowIdResolver(database) }

    /**
     * Links an already-seeded `tvshow` row to the identity layer and returns its internal
     * `show_id`. Tests seed `tvshow` via `tvShowQueries.upsert` and then call this to create the
     * `TRAKT` `tvshow_external_id` row that [showIdResolver] reads, mirroring what
     * `DefaultTvShowsDao.upsert` does in production.
     *
     * Pass [tmdbId] when the Trakt and TMDB ids differ (the test seeded the row by [tmdbId]).
     * When omitted, [traktId] is used for the lookup (tests that use the same value for both).
     */
    protected fun showIdForTraktId(traktId: Long, tmdbId: Long = traktId): Id<ShowId> {
        val showId = database.tvShowQueries.getShowIdByTmdbId(Id<TmdbId>(tmdbId)).executeAsOne()
        database.tvshowExternalIdQueries.insert(
            showId = showId,
            provider = Provider.TRAKT,
            externalId = traktId.toString(),
        )
        return showId
    }

    public fun closeDb() {
        sqlDriver.close()
    }
}
