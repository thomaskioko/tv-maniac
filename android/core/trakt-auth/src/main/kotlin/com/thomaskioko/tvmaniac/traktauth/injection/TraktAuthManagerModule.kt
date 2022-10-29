package com.thomaskioko.tvmaniac.traktauth.injection

import com.thomaskioko.tvmaniac.traktauth.ActivityTraktAuthManager
import com.thomaskioko.tvmaniac.traktauth.TraktAuthManager
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
