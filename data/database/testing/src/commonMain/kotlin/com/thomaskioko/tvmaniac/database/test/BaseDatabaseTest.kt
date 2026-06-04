package com.thomaskioko.tvmaniac.database.test

import app.cash.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.db.DatabaseFactory
import com.thomaskioko.tvmaniac.db.DefaultShowIdResolver
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Provider
import com.thomaskioko.tvmaniac.db.ShowId
import com.thomaskioko.tvmaniac.db.ShowIdResolver
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
     * `show_id`. Tests seed `tvshow` (carrying the public `trakt_id`) and then call this to create
     * the `TRAKT` `tvshow_external_id` row that [showIdResolver] reads, mirroring what
     * `DefaultTvShowsDao.upsert` does in production. Seed the trio (`season`, `episode`,
     * `watched_episodes`) with the returned `show_id`.
     */
    protected fun seedExternalId(traktId: Long): Id<ShowId> {
        val showId = database.tvShowQueries.tvshowByTraktId(Id(traktId)).executeAsOne().id
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
