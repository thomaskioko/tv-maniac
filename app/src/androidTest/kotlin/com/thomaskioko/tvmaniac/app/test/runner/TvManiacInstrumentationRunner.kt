package com.thomaskioko.tvmaniac.app.test.runner

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.thomaskioko.tvmaniac.app.test.TvManiacTestApplication

/**
 * Custom [AndroidJUnitRunner] that swaps the production [com.thomaskioko.tvmaniac.app.TvManicApplication]
 * for [TvManiacTestApplication] at runtime.
 *
 * [TvManiacTestApplication] owns the Metro test graph and the [androidx.work.testing.WorkManagerTestInitHelper]
 * bootstrap. Without this override, instrumentation tests would load the production application class
 * declared in `AndroidManifest.xml` and miss every DI substitution.
 *
 * No manifest change is required: AGP routes [Application] instantiation through
 * [androidx.test.runner.AndroidJUnitRunner.newApplication], so returning [TvManiacTestApplication]
 * here supplants the production `android:name` for instrumentation runs only.
 */
public class TvManiacInstrumentationRunner : AndroidJUnitRunner() {

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?,
    ): Application = super.newApplication(cl, TvManiacTestApplication::class.java.name, context)
}
