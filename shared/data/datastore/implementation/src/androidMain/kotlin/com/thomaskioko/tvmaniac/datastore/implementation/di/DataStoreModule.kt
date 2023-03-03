package com.thomaskioko.tvmaniac.datastore.implementation.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.thomaskioko.tvmaniac.core.util.scope.DefaultCoroutineScope
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.implementation.DatastoreRepositoryImpl
import com.thomaskioko.tvmaniac.datastore.implementation.createDataStore
import com.thomaskioko.tvmaniac.datastore.implementation.dataStoreFileName
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import org.koin.dsl.module
import javax.inject.Singleton

actual fun datastoreModule() : org.koin.core.module.Module = module {  }

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
