package com.thomaskioko.tvmaniac.core.db

import app.cash.sqldelight.db.SqlDriver


class TvManiacDatabaseFactory(
    private val driverFactory: DriverFactory
) {

    fun createDatabase(): TvManiacDatabase {
        return TvManiacDatabase(
            driver = driverFactory.createDriver(),
            showAdapter = Show.Adapter(
                genresAdapter = stringColumnAdapter,
            )
        )
    }
}

expect class DriverFactory {
    fun createDriver(): SqlDriver
}
