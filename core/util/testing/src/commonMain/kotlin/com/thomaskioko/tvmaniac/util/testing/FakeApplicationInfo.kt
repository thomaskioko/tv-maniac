package com.thomaskioko.tvmaniac.util.testing

import com.thomaskioko.tvmaniac.appconfig.ApplicationInfo
import com.thomaskioko.tvmaniac.appconfig.Platform

public object FakeApplicationInfo {
    public val DEFAULT: ApplicationInfo = ApplicationInfo(
        versionName = "1.0.0-test",
        versionCode = 1,
        packageName = "com.thomaskioko.tvmaniac.test",
        debugBuild = true,
        platform = Platform.ANDROID,
    )
}
