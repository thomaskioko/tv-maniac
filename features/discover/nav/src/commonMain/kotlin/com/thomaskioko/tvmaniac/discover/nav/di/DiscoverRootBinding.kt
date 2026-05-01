package com.thomaskioko.tvmaniac.discover.nav.di

import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.discover.nav.DiscoverRoot
import com.thomaskioko.tvmaniac.navigation.NavRoot
import com.thomaskioko.tvmaniac.navigation.NavRootBinding
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(ActivityScope::class)
public interface DiscoverRootBinding {
    public companion object {
        @Provides
        @IntoSet
        public fun provideDiscoverRoot(): NavRoot = DiscoverRoot

        @Provides
        @IntoSet
        public fun provideDiscoverRootBinding(): NavRootBinding<*> =
            NavRootBinding(DiscoverRoot::class, DiscoverRoot.serializer())
    }
}
