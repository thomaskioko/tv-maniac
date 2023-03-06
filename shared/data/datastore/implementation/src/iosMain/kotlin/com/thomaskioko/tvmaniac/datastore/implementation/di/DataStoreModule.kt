package com.thomaskioko.tvmaniac.datastore.implementation.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.implementation.DatastoreRepositoryImpl
import com.thomaskioko.tvmaniac.datastore.implementation.createDataStore
import com.thomaskioko.tvmaniac.datastore.implementation.dataStoreFileName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual fun datastoreModule(): Module = module {
    single { dataStore(get(named("mainScope"))) }
    single<DatastoreRepository> { DatastoreRepositoryImpl(
        dataStore = get(),
        coroutineScope = get(named("mainScope"))
    ) }
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