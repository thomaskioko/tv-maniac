package com.thomaskioko.tvmaniac

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

@HiltAndroidApp
class TvManicApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Napier.base(DebugAntilog())
        }
    }
}
