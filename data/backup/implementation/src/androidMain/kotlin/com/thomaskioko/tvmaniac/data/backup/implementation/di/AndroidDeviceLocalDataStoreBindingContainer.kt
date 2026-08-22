package com.thomaskioko.tvmaniac.data.backup.implementation.di

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.thomaskioko.tvmaniac.core.base.DeviceLocalDataStore
import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import com.thomaskioko.tvmaniac.data.backup.implementation.DEVICE_LOCAL_DATA_STORE_FILE_NAME
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import okio.Path.Companion.toPath

@BindingContainer
@ContributesTo(AppScope::class)
public object AndroidDeviceLocalDataStoreBindingContainer {

    @Provides
    @SingleIn(AppScope::class)
    @DeviceLocalDataStore
    public fun provideDeviceLocalDataStore(
        context: Application,
        @IoCoroutineScope scope: CoroutineScope,
    ): DataStore<Preferences> = PreferenceDataStoreFactory.createWithPath(
        corruptionHandler = null,
        migrations = emptyList(),
        scope = scope,
        produceFile = { context.filesDir.resolve(DEVICE_LOCAL_DATA_STORE_FILE_NAME).absolutePath.toPath() },
    )
}
