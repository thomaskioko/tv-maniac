package com.thomaskioko.tvmaniac.db

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@BindingContainer
@ContributesTo(AppScope::class)
public object DatabaseBindingContainer {

    @Provides
    public fun provideTvManiacDatabase(
        factory: DatabaseFactory,
    ): TvManiacDatabase = factory.createDatabase()
}
