package com.thomaskioko.tvmaniac.settings.implementation.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.thomaskioko.tvmaniac.settings.api.SettingsRepository
import com.thomaskioko.tvmaniac.settings.api.SettingsStateMachine
import com.thomaskioko.tvmaniac.settings.implementation.SettingsRepositoryImpl
import com.thomaskioko.tvmaniac.settings.implementation.createDataStore
import com.thomaskioko.tvmaniac.settings.implementation.dataStoreFileName
import com.thomaskioko.tvmaniac.shared.core.ui.di.DefaultCoroutineScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object SettingsModule {

    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context,
        @DefaultCoroutineScope defaultScope: CoroutineScope
    ): DataStore<Preferences> = createDataStore(
        coroutineScope = defaultScope,
        producePath = { context.filesDir.resolve(dataStoreFileName).absolutePath }
    )

    @Provides
    @Singleton
    fun provideSettingsRepository(
        dataStore: DataStore<Preferences>,
        @DefaultCoroutineScope coroutineScope: CoroutineScope
    ): SettingsRepository = SettingsRepositoryImpl(dataStore, coroutineScope)

    @Provides
    fun provideSettingsStateMachine(
        settingsRepository: SettingsRepository
    ): SettingsStateMachine = SettingsStateMachine(settingsRepository)
}
