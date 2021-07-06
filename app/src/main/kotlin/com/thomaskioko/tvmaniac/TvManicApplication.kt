package com.thomaskioko.tvmaniac

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TvManicApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}