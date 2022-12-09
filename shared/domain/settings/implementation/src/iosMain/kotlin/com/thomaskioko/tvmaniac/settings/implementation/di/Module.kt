package com.thomaskioko.tvmaniac.settings.implementation.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.thomaskioko.tvmaniac.settings.api.SettingsRepository
import com.thomaskioko.tvmaniac.settings.api.SettingsStateMachine
import com.thomaskioko.tvmaniac.settings.implementation.SettingsRepositoryImpl
import com.thomaskioko.tvmaniac.settings.implementation.createDataStore
import com.thomaskioko.tvmaniac.settings.implementation.dataStoreFileName
import kotlinx.coroutines.CoroutineScope
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual fun settingsModule(): Module = module {
    single { dataStore(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get(), get()) }
    single { SettingsStateMachine(get()) }
}

fun dataStore(scope: CoroutineScope): DataStore<Preferences> = createDataStore(
    coroutineScope = scope,
    producePath = {
        val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        requireNotNull(documentDirectory).path + "/$dataStoreFileName"
    }
)