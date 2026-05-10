package com.thomaskioko.tvmaniac.library.nav.di

import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.library.nav.LibraryRoot
import com.thomaskioko.tvmaniac.navigation.NavRoot
import com.thomaskioko.tvmaniac.navigation.NavRootBinding
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(ActivityScope::class)
public interface LibraryRootBinding {
    public companion object {
        @Provides
        @IntoSet
        public fun provideLibraryRoot(): NavRoot = LibraryRoot

        @Provides
        @IntoSet
        public fun provideLibraryRootBinding(): NavRootBinding<*> =
            NavRootBinding(LibraryRoot::class, LibraryRoot.serializer())
    }
}
