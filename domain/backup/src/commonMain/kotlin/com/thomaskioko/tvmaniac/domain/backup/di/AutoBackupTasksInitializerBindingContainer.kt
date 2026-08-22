package com.thomaskioko.tvmaniac.domain.backup.di

import com.thomaskioko.tvmaniac.core.base.AsyncInitializers
import com.thomaskioko.tvmaniac.core.base.Initializer
import com.thomaskioko.tvmaniac.domain.backup.AutoBackupTasksInitializer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@BindingContainer
@ContributesTo(AppScope::class)
public interface AutoBackupTasksInitializerBindingContainer {
    public companion object {
        @Provides
        @IntoSet
        @AsyncInitializers
        public fun provideAutoBackupTasksInitializer(initializer: AutoBackupTasksInitializer): Initializer =
            Initializer { initializer.init() }
    }
}
