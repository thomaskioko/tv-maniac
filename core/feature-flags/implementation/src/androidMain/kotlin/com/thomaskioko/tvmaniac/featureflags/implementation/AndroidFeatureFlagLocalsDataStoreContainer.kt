package com.thomaskioko.tvmaniac.featureflags.implementation

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.thomaskioko.tvmaniac.core.base.FeatureFlagLocalsDataStore
import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import okio.Path.Companion.toPath

private const val FILE_NAME = "feature_flag_locals.preferences_pb"

@BindingContainer
@ContributesTo(AppScope::class)
public object AndroidFeatureFlagLocalsDataStoreContainer {

    @Provides
    @SingleIn(AppScope::class)
    @FeatureFlagLocalsDataStore
    public fun provideLocalsDataStore(
        context: Application,
        @IoCoroutineScope scope: CoroutineScope,
    ): DataStore<Preferences> = PreferenceDataStoreFactory.createWithPath(
        corruptionHandler = null,
        migrations = emptyList(),
        scope = scope,
        produceFile = { context.filesDir.resolve(FILE_NAME).absolutePath.toPath() },
    )
}
