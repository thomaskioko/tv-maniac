package com.thomaskioko.tvmaniac.settings

import org.koin.core.module.Module as KoinModule
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.koin.dsl.module

actual fun settingsDomainModule() : KoinModule = module {  }

@InstallIn(SingletonComponent::class)
@Module
object SettingsModule {

    @Provides
    fun provideSettingsStateMachine(
        datastoreRepository: DatastoreRepository
    ): SettingsStateMachine = SettingsStateMachine(datastoreRepository)
}
