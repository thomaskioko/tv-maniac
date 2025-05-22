package com.thomaskioko.tvmaniac.db

import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo


@ContributesTo(AppScope::class)
interface DatabaseComponent {

  @Provides
  fun provideTvManiacDatabase(
    factory: DatabaseFactory,
  ): TvManiacDatabase = factory.createDatabase()
}
