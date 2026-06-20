package com.thomaskioko.tvmaniac.accountmanager.testing

import com.thomaskioko.tvmaniac.accountmanager.api.ProviderFeatures

public class FakeProviderFeatures(
    override val supportsContinueWatchingFetch: Boolean = false,
    override val supportsFavorites: Boolean = false,
    override val supportsLists: Boolean = false,
    override val supportsCalendar: Boolean = false,
) : ProviderFeatures
