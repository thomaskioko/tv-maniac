package com.thomaskioko.tvmaniac.datastore.implementation

import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

expect interface DataStorePlatformComponent

@ContributesTo(AppScope::class)
interface DataStoreComponent : DataStorePlatformComponent {

  @SingleIn(AppScope::class)
  @Provides
  fun provideDatastoreRepository(bind: DefaultDatastoreRepository): DatastoreRepository = bind
}
