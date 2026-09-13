package com.thomaskioko.tvmaniac.testing.integration.ui

import android.os.Build

public val isRobolectricRuntime: Boolean
    get() = Build.FINGERPRINT.startsWith("robolectric", ignoreCase = true)
