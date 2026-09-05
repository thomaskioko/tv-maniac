package com.thomaskioko.tvmaniac.core.logger.fixture

import com.thomaskioko.tvmaniac.core.logger.CrashlyticsConfiguration

public class FakeCrashlyticsConfiguration(
    override val isConfigured: Boolean = true,
) : CrashlyticsConfiguration {

    private var collectionEnabled: Boolean? = null

    public val enabled: Boolean? get() = collectionEnabled

    override fun setCollectionEnabled(enabled: Boolean) {
        collectionEnabled = enabled
    }
}
