package com.thomaskioko.tvmaniac.core.db

import com.squareup.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.datasource.cache.Season
import com.thomaskioko.tvmaniac.datasource.cache.Show
import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase

class TvManiacDatabaseFactory(
    private val driverFactory: DriverFactory
) {

    fun createDatabase(): TvManiacDatabase {
        return TvManiacDatabase(
            driver = driverFactory.createDriver(),
            showAdapter = Show.Adapter(
                genre_idsAdapter = intAdapter,
                season_idsAdapter = intAdapter,
            ),
            seasonAdapter = Season.Adapter(
                episode_idsAdapter = intAdapter
            )
        )
    }
}

expect class DriverFactory {
    fun createDriver(): SqlDriver
}
