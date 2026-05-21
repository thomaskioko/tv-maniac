package com.thomaskioko.tvmaniac.featureflags.flags

import dev.zacsweers.metro.Qualifier

/**
 * Identifies the Continue Watching Nitro flag. Consumers inject
 * `@param:ContinueWatchingNitroFlagQualifier FeatureFlag<Boolean>` to read the flag without
 * depending on the concrete binding.
 */
@Qualifier
public annotation class ContinueWatchingNitroFlagQualifier
