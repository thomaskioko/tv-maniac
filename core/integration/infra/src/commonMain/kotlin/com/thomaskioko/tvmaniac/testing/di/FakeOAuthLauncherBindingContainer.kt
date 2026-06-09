package com.thomaskioko.tvmaniac.testing.di

import com.thomaskioko.tvmaniac.oauth.api.OAuthLauncher
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
@ContributesTo(AppScope::class)
public object FakeOAuthLauncherBindingContainer {

    @Provides
    @SingleIn(AppScope::class)
    public fun provideFakeOAuthLauncher(): FakeOAuthLauncher = FakeOAuthLauncher()

    @Provides
    public fun provideOAuthLauncher(launcher: FakeOAuthLauncher): OAuthLauncher = launcher
}
