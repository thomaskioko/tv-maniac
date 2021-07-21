package com.thomaskioko.tvmaniac.datasource.cache.db

import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.datasource.cache.Season
import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase
import com.thomaskioko.tvmaniac.datasource.cache.Tv_show
import com.thomaskioko.tvmaniac.datasource.cache.db.adapter.episodeListAdapter
import com.thomaskioko.tvmaniac.datasource.cache.db.adapter.genreListAdapter
import com.thomaskioko.tvmaniac.datasource.cache.db.adapter.seasonsListAdapter

class TvManiacDatabaseFactory(
    private val driverFactory: DriverFactory
) {

    fun createDatabase(): TvManiacDatabase {
        return TvManiacDatabase(
            driver = driverFactory.createDriver(),
            tv_showAdapter = Tv_show.Adapter(
                genre_idsAdapter = genreListAdapter,
                seasonsAdapter = seasonsListAdapter,
                show_categoryAdapter = EnumColumnAdapter(),
                time_windowAdapter = EnumColumnAdapter(),
            ),
            seasonAdapter = Season.Adapter(
                episodesAdapter = episodeListAdapter
            )
        )
    }
}

expect class DriverFactory {
    fun createDriver(): SqlDriver
}