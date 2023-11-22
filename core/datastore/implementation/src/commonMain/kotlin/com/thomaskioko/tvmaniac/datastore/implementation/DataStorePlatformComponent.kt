package com.thomaskioko.tvmaniac.datastore.implementation

import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import me.tatarka.inject.annotations.Provides

expect interface DataStorePlatformComponent

interface DataStoreComponent : DataStorePlatformComponent {

    @ApplicationScope
    @Provides
    fun provideDatastoreRepository(bind: DatastoreRepositoryImpl): DatastoreRepository = bind
}
