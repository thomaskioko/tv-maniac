package com.thomaskioko.tvmaniac.trakt.implementation.injection

import com.thomaskioko.tvmaniac.trakt.api.TraktAuthManager
import com.thomaskioko.tvmaniac.trakt.implementation.ActivityTraktAuthManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
internal abstract class TraktAuthManagerModule {
    @Binds
    abstract fun provideTraktAuthManager(manager: ActivityTraktAuthManager): TraktAuthManager
}
