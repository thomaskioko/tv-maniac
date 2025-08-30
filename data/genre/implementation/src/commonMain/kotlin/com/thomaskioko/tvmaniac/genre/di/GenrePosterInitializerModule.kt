package com.thomaskioko.tvmaniac.genre.di

import com.thomaskioko.tvmaniac.core.base.di.AsyncInitializers
import com.thomaskioko.tvmaniac.genre.GenrePosterInitializer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface GenrePosterInitializerModule {
    @Provides
    @IntoSet
    @AsyncInitializers
    fun provideGenrePosterInitializer(bind: GenrePosterInitializer): () -> Unit = {
        bind.init()
    }
}
