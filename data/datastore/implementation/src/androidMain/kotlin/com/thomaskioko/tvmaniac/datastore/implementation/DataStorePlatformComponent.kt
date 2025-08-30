package com.thomaskioko.tvmaniac.datastore.implementation

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@ContributesTo(AppScope::class)
interface DataStorePlatformComponent {

    @Provides
    @SingleIn(AppScope::class)
    fun provideDataStore(context: Application, scope: AppCoroutineScope): DataStore<Preferences> =
        createDataStore(
            coroutineScope = scope.io,
            produceFile = { context.filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath },
        )
}
