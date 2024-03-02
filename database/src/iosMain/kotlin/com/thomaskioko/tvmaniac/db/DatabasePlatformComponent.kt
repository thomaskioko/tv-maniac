package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.thomaskioko.tvmaniac.core.base.annotations.ApplicationScope
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import me.tatarka.inject.annotations.Provides

actual interface DatabasePlatformComponent {

  @ApplicationScope
  @Provides
  fun provideSqlDriver(): SqlDriver = NativeSqliteDriver(TvManiacDatabase.Schema, "tvShows.db")
}
