package com.thomaskioko.tvmaniac.db

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface DatabaseComponent {

    @Provides
    fun provideTvManiacDatabase(
        factory: DatabaseFactory,
    ): TvManiacDatabase = factory.createDatabase()
}
