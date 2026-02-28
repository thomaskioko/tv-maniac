package com.thomaskioko.tvmaniac.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.thomaskioko.tvmaniac.util.api.ApplicationInfo
import com.thomaskioko.tvmaniac.util.api.BuildConfig
import com.thomaskioko.tvmaniac.util.api.Platform
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo

@ContributesTo(AppScope::class)
public interface AndroidUtilComponent {

    @Provides
    public fun provideApplicationInfo(context: Context): ApplicationInfo {
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

        // TODO:: Read versionName and versionCode from BuildConfig
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
