package com.thomaskioko.tvmaniac.core.logger.fixture

import com.thomaskioko.tvmaniac.core.logger.CrashlyticsCollection

public class FakeCrashlyticsCollection : CrashlyticsCollection {

    private var collectionEnabled: Boolean? = null

    public val enabled: Boolean? get() = collectionEnabled

    override fun setEnabled(enabled: Boolean) {
        collectionEnabled = enabled
    }
}
