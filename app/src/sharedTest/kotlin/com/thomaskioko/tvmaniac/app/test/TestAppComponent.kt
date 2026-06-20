package com.thomaskioko.tvmaniac.app.test

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.thomaskioko.tvmaniac.core.base.AppPreferencesDataStore
import com.thomaskioko.tvmaniac.core.base.IsDebugBuild
import com.thomaskioko.tvmaniac.featureflags.testing.FakeFeatureFlagsRemoteConfig
import com.thomaskioko.tvmaniac.oauth.api.AuthStateHolder
import com.thomaskioko.tvmaniac.oauth.testing.FakeOAuthLauncher
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph(AppScope::class)
interface TestAppComponent {

    val traktAuthRepository: FakeTraktAuthRepository
    val oAuthLauncher: FakeOAuthLauncher
    val authStateHolder: AuthStateHolder
    val featureFlagsRemoteConfig: FakeFeatureFlagsRemoteConfig

    @AppPreferencesDataStore
    val dataStore: DataStore<Preferences>

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(
            @Provides application: Application,
            @Provides @IsDebugBuild isDebug: Boolean,
        ): TestAppComponent
    }
}
