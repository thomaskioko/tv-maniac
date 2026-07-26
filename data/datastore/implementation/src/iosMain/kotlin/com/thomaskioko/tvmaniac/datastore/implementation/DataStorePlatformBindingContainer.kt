package com.thomaskioko.tvmaniac.datastore.implementation

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.thomaskioko.tvmaniac.core.base.AppPreferencesDataStore
import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSProcessInfo
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

private const val CLEAR_STATE_ENV = "TVMANIAC_CLEAR_STATE"

@BindingContainer
@ContributesTo(AppScope::class)
public object DataStorePlatformBindingContainer {

    @OptIn(ExperimentalForeignApi::class)
    @Provides
    @SingleIn(AppScope::class)
    @AppPreferencesDataStore
    public fun provideDataStore(
        @IoCoroutineScope scope: CoroutineScope,
    ): DataStore<Preferences> {
        val path = preferencesPath()
        if (shouldStartFromEmptyPreferences()) {
            clearDataStoreReference()
            NSFileManager.defaultManager.removeItemAtPath(path, null)
        }
        return createDataStore(
            coroutineScope = scope,
            produceFile = { path },
        )
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun preferencesPath(): String {
        val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        return requireNotNull(documentDirectory).path + "/$DATA_STORE_FILE_NAME"
    }

    /** Deleting here also clears the reference `createDataStore` caches for the whole process. */
    private fun shouldStartFromEmptyPreferences(): Boolean =
        NSProcessInfo.processInfo.environment[CLEAR_STATE_ENV] as String? == "1"
}
