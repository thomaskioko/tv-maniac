package com.thomaskioko.tvmaniac.core.notifications.api

import androidx.annotation.DrawableRes

public interface NotificationIconProvider {
    @get:DrawableRes
    public val smallIconResId: Int
}
