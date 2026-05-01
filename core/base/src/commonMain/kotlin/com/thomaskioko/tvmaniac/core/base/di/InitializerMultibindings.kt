package com.thomaskioko.tvmaniac.core.base.di

import com.thomaskioko.tvmaniac.core.base.AsyncInitializers
import com.thomaskioko.tvmaniac.core.base.Initializer
import com.thomaskioko.tvmaniac.core.base.Initializers
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds

@ContributesTo(AppScope::class)
public interface InitializerMultibindings {
    @Initializers
    @Multibinds
    public fun initializers(): Set<Initializer>

    @AsyncInitializers
    @Multibinds
    public fun asyncInitializers(): Set<Initializer>
}
