package com.thomaskioko.tvmaniac.db

import android.app.Application
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface DatabasePlatformComponent {

  @Provides
  @SingleIn(AppScope::class)
  fun provideSqlDriver(application: Application): SqlDriver =
    AndroidSqliteDriver(
      schema = TvManiacDatabase.Schema,
      context = application,
      name = "tvShows.db",
    )
}
