package com.thomaskioko.tvmaniac.datastore.implementation

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope

@BindingContainer
@ContributesTo(AppScope::class)
public object DataStorePlatformBindingContainer {

    @Provides
    @SingleIn(AppScope::class)
    public fun provideDataStore(
        context: Application,
        @IoCoroutineScope scope: CoroutineScope,
    ): DataStore<Preferences> = createDataStore(
        coroutineScope = scope,
        produceFile = { context.filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath },
    )
}
