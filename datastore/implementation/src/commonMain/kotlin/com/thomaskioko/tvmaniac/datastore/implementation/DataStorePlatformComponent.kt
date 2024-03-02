package com.thomaskioko.tvmaniac.datastore.implementation

import com.thomaskioko.tvmaniac.core.base.annotations.ApplicationScope
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import me.tatarka.inject.annotations.Provides

expect interface DataStorePlatformComponent

interface DataStoreComponent : DataStorePlatformComponent {

  @ApplicationScope
  @Provides
  fun provideDatastoreRepository(bind: DefaultDatastoreRepository): DatastoreRepository = bind
}
