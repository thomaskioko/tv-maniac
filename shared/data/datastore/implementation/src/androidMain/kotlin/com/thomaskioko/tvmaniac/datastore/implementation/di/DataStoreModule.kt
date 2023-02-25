package com.thomaskioko.tvmaniac.datastore.implementation.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.implementation.DatastoreRepositoryImpl
import com.thomaskioko.tvmaniac.datastore.implementation.createDataStore
import com.thomaskioko.tvmaniac.datastore.implementation.dataStoreFileName
import com.thomaskioko.tvmaniac.core.util.scope.DefaultCoroutineScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataStoreModule {

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
    ): DatastoreRepository = DatastoreRepositoryImpl(dataStore, coroutineScope)

}
