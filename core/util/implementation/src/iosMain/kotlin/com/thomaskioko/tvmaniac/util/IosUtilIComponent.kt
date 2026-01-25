package com.thomaskioko.tvmaniac.util

import com.thomaskioko.tvmaniac.buildconfig.api.BuildConfig
import com.thomaskioko.tvmaniac.util.api.ApplicationInfo
import com.thomaskioko.tvmaniac.util.api.Platform
import me.tatarka.inject.annotations.Provides
import platform.Foundation.NSBundle
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo

@ContributesTo(AppScope::class)
public interface IosUtilIComponent {

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
