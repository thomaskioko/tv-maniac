package com.thomaskioko.tvmaniac.datasource.cache.db

import com.squareup.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.datasource.cache.Show
import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase
import com.thomaskioko.tvmaniac.datasource.cache.Tv_season
import com.thomaskioko.tvmaniac.datasource.cache.db.adapter.intAdapter

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
            tv_seasonAdapter = Tv_season.Adapter(
                episode_idsAdapter = intAdapter
            )
        )
    }
}

expect class DriverFactory {
    fun createDriver(): SqlDriver
}
