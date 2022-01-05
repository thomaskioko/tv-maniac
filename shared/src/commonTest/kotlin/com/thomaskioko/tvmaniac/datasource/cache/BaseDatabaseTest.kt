package com.thomaskioko.tvmaniac.datasource.cache

import com.squareup.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.datasource.cache.db.adapter.intAdapter
import kotlin.test.AfterTest

expect fun inMemorySqlDriver(): SqlDriver

/**
 * Creates an in-memory database and closes it before and after each test.
 * This class exists because JUnit rules aren't a thing (yet) in Kotlin tests.
 *
 * The name of this class is not a typo.
 */
abstract class BaseDatabaseTest {
    private val sqlDriver: SqlDriver = inMemorySqlDriver()
    protected open val database: TvManiacDatabase = TvManiacDatabase(
        driver = sqlDriver,
        showAdapter = Show.Adapter(
            genre_idsAdapter = intAdapter,
            season_idsAdapter = intAdapter,
        ),
        seasonAdapter = Season.Adapter(
            episode_idsAdapter = intAdapter
        )
    )

    @AfterTest
    fun closeDb() {
        sqlDriver.close()
    }
}
