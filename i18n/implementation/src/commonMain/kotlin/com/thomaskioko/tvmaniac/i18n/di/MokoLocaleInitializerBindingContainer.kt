package com.thomaskioko.tvmaniac.i18n.di

import com.thomaskioko.tvmaniac.core.base.Initializers
import com.thomaskioko.tvmaniac.i18n.MokoLocaleInitializer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@BindingContainer
@ContributesTo(AppScope::class)
public object MokoLocaleInitializerBindingContainer {
    @Provides
    @IntoSet
    @Initializers
    public fun provideMokoLocaleInitializer(bind: MokoLocaleInitializer): () -> Unit = { bind.init() }
}
