package com.thomaskioko.tvmaniac.core.db

import app.cash.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.db.InstantColumnAdapter
import com.thomaskioko.tvmaniac.db.stringColumnAdapter
import kotlin.test.AfterTest

expect fun inMemorySqlDriver(): SqlDriver

/**
 * Creates an in-memory database and closes it before and after each test.
 * This class exists because JUnit rules aren't a thing (yet) in Kotlin tests.
 *
 */
abstract class BaseDatabaseTest {
    private val sqlDriver: SqlDriver = inMemorySqlDriver()
    protected open val database: TvManiacDatabase = TvManiacDatabase(
        driver = sqlDriver,
        showAdapter = Show.Adapter(
            genresAdapter = stringColumnAdapter,
        ),
        last_requestsAdapter = Last_requests.Adapter(
            timestampAdapter = InstantColumnAdapter,
        ),
    )

    @AfterTest
    fun closeDb() {
        sqlDriver.close()
    }
}
