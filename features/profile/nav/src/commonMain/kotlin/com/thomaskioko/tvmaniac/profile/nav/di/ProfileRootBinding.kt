package com.thomaskioko.tvmaniac.profile.nav.di

import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.NavRoot
import com.thomaskioko.tvmaniac.navigation.NavRootBinding
import com.thomaskioko.tvmaniac.profile.nav.ProfileRoot
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(ActivityScope::class)
public interface ProfileRootBinding {
    public companion object {
        @Provides
        @IntoSet
        public fun provideProfileRoot(): NavRoot = ProfileRoot

        @Provides
        @IntoSet
        public fun provideProfileRootBinding(): NavRootBinding<*> =
            NavRootBinding(ProfileRoot::class, ProfileRoot.serializer())
    }
}
