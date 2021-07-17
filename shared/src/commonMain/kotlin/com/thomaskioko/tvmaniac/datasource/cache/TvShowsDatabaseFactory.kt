package com.thomaskioko.tvmaniac.datasource.cache

import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.datasource.cache.adapter.episodeListAdapter
import com.thomaskioko.tvmaniac.datasource.cache.adapter.genreListAdapter
import com.thomaskioko.tvmaniac.datasource.cache.adapter.seasonsListAdapter

class TvShowsDatabaseFactory(
    private val driverFactory: DriverFactory
) {

    fun createDatabase(): TvManiacDatabase {
        return TvManiacDatabase(
            driver = driverFactory.createDriver(),
            tv_showAdapter = Tv_show.Adapter(
                genre_idsAdapter = genreListAdapter,
                show_categoryAdapter = EnumColumnAdapter(),
                show_seasonsAdapter = seasonsListAdapter,
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