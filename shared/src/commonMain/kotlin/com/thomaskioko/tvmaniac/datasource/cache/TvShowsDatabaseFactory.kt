package com.thomaskioko.tvmaniac.datasource.cache

import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.datasource.cache.adapter.genreListAdapter

class TvShowsDatabaseFactory(
    private val driverFactory: DriverFactory
) {

    fun createDatabase(): TvManiacDatabase {
        return TvManiacDatabase(
            driver = driverFactory.createDriver(),
            tv_showAdapter = Tv_show.Adapter(
                genre_idsAdapter = genreListAdapter,
                show_categoryAdapter = EnumColumnAdapter()
            )
        )
    }
}

expect class DriverFactory {
    fun createDriver(): SqlDriver
}