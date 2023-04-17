package com.thomaskioko.tvmaniac.datastore.implementation

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.thomaskioko.tvmaniac.util.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import me.tatarka.inject.annotations.Provides

actual interface DataStorePlatformComponent {

    @ApplicationScope
    @Provides
    fun provideDataStore(
        context: Application,
        scope: AppCoroutineScope
    ): DataStore<Preferences> = createDataStore(
        coroutineScope = scope.io,
        producePath = { context.filesDir.resolve(dataStoreFileName).absolutePath }
    )

    @ApplicationScope
    @Provides
    fun provideDatastoreRepository(bind: DatastoreRepositoryImpl): DatastoreRepository = bind

}