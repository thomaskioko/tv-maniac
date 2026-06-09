package com.thomaskioko.tvmaniac.testing.integration.bindings

import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.oauth.api.OAuthLauncher
import com.thomaskioko.tvmaniac.oauth.implementation.AndroidOAuthLauncher
import com.thomaskioko.tvmaniac.oauth.testing.FakeOAuthLauncher
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
@ContributesTo(
    ActivityScope::class,
    replaces = [AndroidOAuthLauncher::class],
)
public object TestOAuthLauncherBindingContainer {

    @Provides
    @SingleIn(ActivityScope::class)
    public fun provideOAuthLauncher(
        fakeOAuthLauncher: FakeOAuthLauncher,
    ): OAuthLauncher = fakeOAuthLauncher
}
