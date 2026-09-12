package com.thomaskioko.tvmaniac.presenter.root

import kotlinx.coroutines.flow.Flow

public fun interface LiquidGlassAvailability {
    public fun observe(): Flow<Boolean>
}
