package com.thomaskioko.tvmaniac.datastore.implementation

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
public interface DataStorePlatformComponent {

    @Provides
    @SingleIn(AppScope::class)
    public fun provideDataStore(context: Application, scope: AppCoroutineScope): DataStore<Preferences> =
        createDataStore(
            coroutineScope = scope.io,
            produceFile = { context.filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath },
        )
}
