package com.thomaskioko.tvmaniac

import android.app.Application
import com.thomaskioko.tvmaniac.injection.ApplicationScope
import com.thomaskioko.tvmaniac.shared.core.firebaseconfig.api.FeatureFlagsRemoteConfig
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class TvManicApplication : Application() {

    @Inject
    lateinit var featureFlagsRemoteConfig: FeatureFlagsRemoteConfig

    @Inject
    @ApplicationScope
    lateinit var coroutineScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()

        coroutineScope.launch {
            featureFlagsRemoteConfig.fetch()
        }
    }
}
