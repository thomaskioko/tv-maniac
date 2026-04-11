package com.thomaskioko.tvmaniac.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.thomaskioko.tvmaniac.core.base.ApplicationContext
import com.thomaskioko.tvmaniac.util.api.ApplicationInfo
import com.thomaskioko.tvmaniac.util.api.BuildConfig
import com.thomaskioko.tvmaniac.util.api.Platform
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@BindingContainer
@ContributesTo(AppScope::class)
public object AndroidUtilBindingContainer {

    @Provides
    public fun provideApplicationInfo(@ApplicationContext context: Context): ApplicationInfo {
        val packageManager = context.packageManager
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(
                context.packageName,
                PackageManager.PackageInfoFlags.of(0),
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(context.packageName, 0)
        }

        return ApplicationInfo(
            versionName = packageInfo.versionName ?: "Unknown",
            versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode
            },
            packageName = context.packageName,
            debugBuild = BuildConfig.IS_DEBUG,
            platform = Platform.ANDROID,
        )
    }
}
