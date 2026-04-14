package com.thomaskioko.tvmaniac.domain.library.di

import com.thomaskioko.tvmaniac.core.base.AsyncInitializers
import com.thomaskioko.tvmaniac.domain.library.SyncTasksInitializer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@BindingContainer
@ContributesTo(AppScope::class)
public interface SyncTasksInitializerBindingContainer {
    public companion object {
        @Provides
        @IntoSet
        @AsyncInitializers
        public fun provideSyncTasksInitializer(bind: SyncTasksInitializer): () -> Unit = { bind.init() }
    }
}
