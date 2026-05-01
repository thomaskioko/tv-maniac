package com.thomaskioko.tvmaniac.testing.integration.bindings

import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import com.thomaskioko.tvmaniac.traktauth.implementation.AndroidTraktAuthManager
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthManager
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
@ContributesTo(
    ActivityScope::class,
    replaces = [AndroidTraktAuthManager::class],
)
public object TestTraktAuthManagerBindingContainer {

    @Provides
    @SingleIn(ActivityScope::class)
    public fun provideTraktAuthManager(
        fakeTraktAuthManager: FakeTraktAuthManager,
    ): TraktAuthManager = fakeTraktAuthManager
}
