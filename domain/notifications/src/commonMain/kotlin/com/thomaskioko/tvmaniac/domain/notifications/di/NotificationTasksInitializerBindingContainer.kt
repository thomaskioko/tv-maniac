package com.thomaskioko.tvmaniac.domain.notifications.di

import com.thomaskioko.tvmaniac.core.base.AsyncInitializers
import com.thomaskioko.tvmaniac.core.base.Initializer
import com.thomaskioko.tvmaniac.domain.notifications.NotificationTasksInitializer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@BindingContainer
@ContributesTo(AppScope::class)
public interface NotificationTasksInitializerBindingContainer {
    public companion object {
        @Provides
        @IntoSet
        @AsyncInitializers
        public fun provideNotificationTasksInitializer(impl: NotificationTasksInitializer): Initializer = Initializer { impl.init() }
    }
}
