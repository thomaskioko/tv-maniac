package com.thomaskioko.tvmaniac.datastore.implementation.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.implementation.DatastoreRepositoryImpl
import com.thomaskioko.tvmaniac.datastore.implementation.createDataStore
import com.thomaskioko.tvmaniac.datastore.implementation.dataStoreFileName
import kotlinx.coroutines.CoroutineScope
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual fun datastoreModule(): Module = module {
    single { dataStore(get()) }
    single<DatastoreRepository> { DatastoreRepositoryImpl(get(), get()) }
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