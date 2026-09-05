package com.thomaskioko.tvmaniac.core.logger

public interface CrashlyticsConfiguration {
    public val isConfigured: Boolean

    public fun setCollectionEnabled(enabled: Boolean)
}
