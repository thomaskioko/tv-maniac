package com.thomaskioko.tvmaniac.presenter.root

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@ContributesBinding(AppScope::class)
public class AndroidLiquidGlassAvailability : LiquidGlassAvailability {
    override fun observe(): Flow<Boolean> = flowOf(false)
}
