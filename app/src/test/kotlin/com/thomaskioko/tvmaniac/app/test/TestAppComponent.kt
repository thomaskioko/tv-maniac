package com.thomaskioko.tvmaniac.app.test

import android.app.Application
import com.thomaskioko.tvmaniac.core.base.TmdbApi
import com.thomaskioko.tvmaniac.core.base.TraktApi
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import io.ktor.client.HttpClient

@DependencyGraph(AppScope::class)
interface TestAppComponent {

    @TmdbApi
    val tmdbClient: HttpClient

    @TraktApi
    val traktClient: HttpClient

    val traktAuthRepository: FakeTraktAuthRepository

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides application: Application): TestAppComponent
    }
}
