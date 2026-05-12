package com.thomaskioko.tvmaniac.appconfig

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import platform.Foundation.NSBundle

@BindingContainer
@ContributesTo(AppScope::class)
public object IosAppConfigBindingContainer {

    @Provides
    public fun provideAppMetadata(): AppMetadata {
        val bundle = NSBundle.mainBundle
        val versionName = bundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String
        val versionCode = bundle.objectForInfoDictionaryKey("CFBundleVersion") as? String

        return AppMetadata(
            versionName = versionName ?: "Unknown",
            versionCode = versionCode?.toIntOrNull() ?: 0,
            packageName = bundle.bundleIdentifier ?: "Unknown",
            platform = Platform.IOS,
        )
    }
}
