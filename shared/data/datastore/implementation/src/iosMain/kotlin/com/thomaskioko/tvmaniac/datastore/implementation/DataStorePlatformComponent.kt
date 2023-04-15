package com.thomaskioko.tvmaniac.datastore.implementation

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.thomaskioko.tvmaniac.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.base.scope.ApplicationScope
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.implementation.DatastoreRepositoryImpl
import com.thomaskioko.tvmaniac.datastore.implementation.createDataStore
import com.thomaskioko.tvmaniac.datastore.implementation.dataStoreFileName
import me.tatarka.inject.annotations.Provides
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual interface DataStorePlatformComponent {

    @ApplicationScope
    @Provides
    fun provideDataStore(
        dispatchers: AppCoroutineScope
    ): DataStore<Preferences> = createDataStore(
        coroutineScope = dispatchers.io,
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

    @ApplicationScope
    @Provides
    fun provideDatastoreRepository(bind: DatastoreRepositoryImpl): DatastoreRepository = bind

}