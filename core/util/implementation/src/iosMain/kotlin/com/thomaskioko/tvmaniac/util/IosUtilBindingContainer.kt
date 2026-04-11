package com.thomaskioko.tvmaniac.util

import com.thomaskioko.tvmaniac.util.api.ApplicationInfo
import com.thomaskioko.tvmaniac.util.api.BuildConfig
import com.thomaskioko.tvmaniac.util.api.Platform
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import platform.Foundation.NSBundle

@BindingContainer
@ContributesTo(AppScope::class)
public object IosUtilBindingContainer {

    @Provides
    public fun provideApplicationInfo(): ApplicationInfo {
        val bundle = NSBundle.mainBundle
        val versionName = bundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String
        val versionCode = bundle.objectForInfoDictionaryKey("CFBundleVersion") as? String

        return ApplicationInfo(
            versionName = versionName ?: "Unknown",
            versionCode = versionCode?.toIntOrNull() ?: 0,
            packageName = bundle.bundleIdentifier ?: "Unknown",
            debugBuild = BuildConfig.IS_DEBUG,
            platform = Platform.IOS,
        )
    }
}
