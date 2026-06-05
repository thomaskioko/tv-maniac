package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.db.SqlDriver

internal const val DATABASE_NAME: String = "tvShows.db"

public interface DatabaseDriverBuilder {

    public fun build(): SqlDriver

    public fun deleteDatabase()
}
