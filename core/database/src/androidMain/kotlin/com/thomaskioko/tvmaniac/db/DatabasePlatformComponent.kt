package com.thomaskioko.tvmaniac.db

import android.app.Application
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.thomaskioko.tvmaniac.core.base.annotations.ApplicationScope
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import me.tatarka.inject.annotations.Provides

actual interface DatabasePlatformComponent {

  @ApplicationScope
  @Provides
  fun provideSqlDriver(application: Application): SqlDriver =
    AndroidSqliteDriver(
      schema = TvManiacDatabase.Schema,
      context = application,
      name = "tvShows.db",
    )
}
