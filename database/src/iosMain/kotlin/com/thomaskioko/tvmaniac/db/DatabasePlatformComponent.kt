package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface DatabasePlatformComponent {

  @Provides
  @SingleIn(AppScope::class)
  fun provideSqlDriver(): SqlDriver = NativeSqliteDriver(TvManiacDatabase.Schema, "tvShows.db")
}
