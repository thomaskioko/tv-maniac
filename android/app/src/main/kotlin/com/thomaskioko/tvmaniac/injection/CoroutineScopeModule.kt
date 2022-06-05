package com.thomaskioko.tvmaniac.injection

import com.thomaskioko.tvmaniac.shared.core.ui.di.IoCoroutineScope
import com.thomaskioko.tvmaniac.shared.core.ui.di.MainCoroutineScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

@InstallIn(SingletonComponent::class)
@Module
object CoroutineScopeModule {

    @Provides
    @MainCoroutineScope
    fun provideMainCoroutineScope(): CoroutineScope = CoroutineScope(Job() + Dispatchers.Main)

    @Provides
    @IoCoroutineScope
    fun provideIOCoroutineScope(): CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)
}
