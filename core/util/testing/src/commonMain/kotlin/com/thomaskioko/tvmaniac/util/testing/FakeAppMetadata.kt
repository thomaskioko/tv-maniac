package com.thomaskioko.tvmaniac.util.testing

import com.thomaskioko.tvmaniac.appconfig.AppMetadata
import com.thomaskioko.tvmaniac.appconfig.DebugConfig
import com.thomaskioko.tvmaniac.appconfig.Platform

public object FakeAppMetadata {
    public val DEFAULT: AppMetadata = AppMetadata(
        versionName = "1.0.0-test",
        versionCode = 1,
        packageName = "com.thomaskioko.tvmaniac.test",
        platform = Platform.ANDROID,
    )
}

public class FakeDebugConfig(override val isDebug: Boolean = true) : DebugConfig
