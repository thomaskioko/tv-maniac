package com.thomaskioko.tvmaniac.core.base.di

import android.app.Application
import android.content.Context
import com.thomaskioko.tvmaniac.core.base.ApplicationContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@BindingContainer
@ContributesTo(AppScope::class)
public object BaseAndroidBindingContainer {

    @Provides
    @ApplicationContext
    public fun provideApplicationContext(application: Application): Context = application
}
