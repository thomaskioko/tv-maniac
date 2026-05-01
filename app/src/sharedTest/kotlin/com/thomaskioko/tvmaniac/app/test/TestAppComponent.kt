package com.thomaskioko.tvmaniac.app.test

import android.app.Application
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthManager
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph(AppScope::class)
interface TestAppComponent {

    val traktAuthRepository: FakeTraktAuthRepository
    val traktAuthManager: FakeTraktAuthManager
    val datastoreRepository: DatastoreRepository

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides application: Application): TestAppComponent
    }
}
