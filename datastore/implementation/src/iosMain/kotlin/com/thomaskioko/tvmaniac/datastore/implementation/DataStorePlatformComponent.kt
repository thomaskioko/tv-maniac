package com.thomaskioko.tvmaniac.datastore.implementation

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.thomaskioko.tvmaniac.core.base.annotations.ApplicationScope
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import me.tatarka.inject.annotations.Provides
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual interface DataStorePlatformComponent {

  @ApplicationScope
  @Provides
  fun provideDataStore(dispatchers: AppCoroutineScope): DataStore<Preferences> =
    createDataStore(
      coroutineScope = dispatchers.io,
      produceFile = {
        val documentDirectory: NSURL? =
          NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
          )
        requireNotNull(documentDirectory).path + "/$dataStoreFileName"
      },
    )
}
