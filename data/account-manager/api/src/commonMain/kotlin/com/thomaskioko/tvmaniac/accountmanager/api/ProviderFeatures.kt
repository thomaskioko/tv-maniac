package com.thomaskioko.tvmaniac.accountmanager.api

public interface ProviderFeatures {
    public val supportsContinueWatchingFetch: Boolean
    public val supportsFavorites: Boolean
    public val supportsLists: Boolean
    public val supportsCalendar: Boolean
}

public object NoProviderFeatures : ProviderFeatures {
    override val supportsContinueWatchingFetch: Boolean = false
    override val supportsFavorites: Boolean = false
    override val supportsLists: Boolean = false
    override val supportsCalendar: Boolean = false
}
