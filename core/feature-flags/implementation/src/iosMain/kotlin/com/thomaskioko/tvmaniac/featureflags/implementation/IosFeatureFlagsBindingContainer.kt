package com.thomaskioko.tvmaniac.featureflags.implementation

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.thomaskioko.tvmaniac.core.base.AsyncInitializers
import com.thomaskioko.tvmaniac.core.base.FeatureFlagLocalsDataStore
import com.thomaskioko.tvmaniac.core.base.Initializer
import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

private const val LOG_TAG = "FeatureFlagInitializer"
private const val FILE_NAME = "feature_flag_locals.preferences_pb"

@BindingContainer
@ContributesTo(AppScope::class)
public object IosFeatureFlagsBindingContainer {

    @Provides
    @IntoSet
    @AsyncInitializers
    public fun provideRemoteConfigInitializer(
        bind: IosRemoteConfig,
        @IoCoroutineScope scope: CoroutineScope,
        logger: Logger,
    ): Initializer = Initializer {
        scope.launch {
            runCatching {
                bind.setup()
                bind.refresh()
            }.onFailure { logger.error(LOG_TAG, "feature flag init failed", it) }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    @Provides
    @SingleIn(AppScope::class)
    @FeatureFlagLocalsDataStore
    public fun provideLocalsDataStore(
        @IoCoroutineScope scope: CoroutineScope,
    ): DataStore<Preferences> = PreferenceDataStoreFactory.createWithPath(
        corruptionHandler = null,
        migrations = emptyList(),
        scope = scope,
        produceFile = {
            val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null,
            )
            (requireNotNull(documentDirectory).path + "/$FILE_NAME").toPath()
        },
    )
}
