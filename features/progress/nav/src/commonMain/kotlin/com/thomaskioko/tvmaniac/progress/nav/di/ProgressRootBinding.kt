package com.thomaskioko.tvmaniac.progress.nav.di

import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.NavRoot
import com.thomaskioko.tvmaniac.navigation.NavRootBinding
import com.thomaskioko.tvmaniac.progress.nav.ProgressRoot
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(ActivityScope::class)
public interface ProgressRootBinding {
    public companion object {
        @Provides
        @IntoSet
        public fun provideProgressRoot(): NavRoot = ProgressRoot

        @Provides
        @IntoSet
        public fun provideProgressRootBinding(): NavRootBinding<*> =
            NavRootBinding(ProgressRoot::class, ProgressRoot.serializer())
    }
}
