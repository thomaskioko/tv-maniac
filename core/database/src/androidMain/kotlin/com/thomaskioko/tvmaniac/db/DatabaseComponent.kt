package com.thomaskioko.tvmaniac.db

import android.app.Application
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.thomaskioko.tvmaniac.core.db.Last_requests
import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import me.tatarka.inject.annotations.Provides

actual interface DatabaseComponent {

    @ApplicationScope
    @Provides
    fun provideSqlDriver(
        application: Application,
    ): SqlDriver = AndroidSqliteDriver(
        schema = TvManiacDatabase.Schema,
        context = application,
        name = "tvShows.db",
    )

    @ApplicationScope
    @Provides
    fun provideTvManiacDatabase(
        sqlDriver: SqlDriver,
    ): TvManiacDatabase = TvManiacDatabase(
        driver = sqlDriver,
        showAdapter = Show.Adapter(
            genresAdapter = stringColumnAdapter,
        ),
        last_requestsAdapter = Last_requests.Adapter(
            timestampAdapter = InstantColumnAdapter
        )
    )
}
