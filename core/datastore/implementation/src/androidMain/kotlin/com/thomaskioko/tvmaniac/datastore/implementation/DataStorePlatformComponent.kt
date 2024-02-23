package com.thomaskioko.tvmaniac.datastore.implementation

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.thomaskioko.tvmaniac.core.base.annotations.ApplicationScope
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import me.tatarka.inject.annotations.Provides

actual interface DataStorePlatformComponent {

  @ApplicationScope
  @Provides
  fun provideDataStore(context: Application, scope: AppCoroutineScope): DataStore<Preferences> =
    createDataStore(
      coroutineScope = scope.io,
      produceFile = { context.filesDir.resolve(dataStoreFileName).absolutePath },
    )
}
