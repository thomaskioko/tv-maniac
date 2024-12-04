package com.thomaskioko.tvmaniac.datastore.implementation

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo

@ContributesTo(AppScope::class)
interface DataStorePlatformComponent {

  @Provides
  fun provideDataStore(context: Application, scope: AppCoroutineScope): DataStore<Preferences> =
    createDataStore(
      coroutineScope = scope.io,
      produceFile = { context.filesDir.resolve(dataStoreFileName).absolutePath },
    )
}
