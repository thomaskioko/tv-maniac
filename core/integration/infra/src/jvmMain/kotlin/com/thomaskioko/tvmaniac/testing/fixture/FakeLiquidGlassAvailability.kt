package com.thomaskioko.tvmaniac.testing.fixture

import com.thomaskioko.tvmaniac.presenter.root.LiquidGlassAvailability
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

public class FakeLiquidGlassAvailability : LiquidGlassAvailability {
    private val enabled = MutableStateFlow(false)

    public fun setEnabled(value: Boolean) {
        enabled.value = value
    }

    override fun observe(): Flow<Boolean> = enabled.asStateFlow()
}
