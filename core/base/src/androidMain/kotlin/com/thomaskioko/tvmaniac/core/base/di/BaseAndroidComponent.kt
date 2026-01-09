package com.thomaskioko.tvmaniac.core.base.di

import android.app.Application
import android.content.Context
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo

@ContributesTo(AppScope::class)
public interface BaseAndroidComponent {

    @Provides
    public fun provideContext(application: Application): Context = application
}
