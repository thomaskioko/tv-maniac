package com.thomaskioko.tvmaniac.datasource.cache

import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.datasource.cache.adapter.episodeListAdapter
import com.thomaskioko.tvmaniac.datasource.cache.adapter.genreListAdapter
import com.thomaskioko.tvmaniac.datasource.cache.adapter.seasonsListAdapter
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
        tv_showAdapter = Tv_show.Adapter(
            genre_idsAdapter = genreListAdapter,
            show_categoryAdapter = EnumColumnAdapter(),
            show_seasonsAdapter = seasonsListAdapter
        ),
        seasonAdapter = Season.Adapter(
            episodesAdapter = episodeListAdapter
        )
    )

    @AfterTest
    fun closeDb() {
        sqlDriver.close()
    }
}
