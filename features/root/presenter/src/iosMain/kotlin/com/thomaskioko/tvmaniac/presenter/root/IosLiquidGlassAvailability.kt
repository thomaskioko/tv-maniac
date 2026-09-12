package com.thomaskioko.tvmaniac.presenter.root

import com.thomaskioko.tvmaniac.featureflags.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.flags.LiquidGlassFlagQualifier
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.Flow

@ContributesBinding(AppScope::class)
public class IosLiquidGlassAvailability(
    @LiquidGlassFlagQualifier private val flag: FeatureFlag<Boolean>,
) : LiquidGlassAvailability {
    override fun observe(): Flow<Boolean> = flag.observe()
}
