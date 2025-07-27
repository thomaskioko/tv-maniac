package com.thomaskioko.tvmaniac.i18n.di

import com.thomaskioko.tvmaniac.core.base.AppInitializer
import com.thomaskioko.tvmaniac.i18n.MokoLocaleInitializer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
public interface MokoLocaleInitializerModule {
    @Provides
    @IntoSet
    public fun provideMokoLocaleInitializer(impl: MokoLocaleInitializer): AppInitializer = impl
}
