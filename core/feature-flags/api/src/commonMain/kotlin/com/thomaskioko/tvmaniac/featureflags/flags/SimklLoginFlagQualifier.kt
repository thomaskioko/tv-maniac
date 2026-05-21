package com.thomaskioko.tvmaniac.featureflags.flags

import dev.zacsweers.metro.Qualifier

/**
 * Identifies the Simkl Login flag. Consumers inject
 * `@SimklLoginFlagQualifier FeatureFlag<Boolean>` to read the flag without depending on the
 * concrete binding.
 */
@Qualifier
public annotation class SimklLoginFlagQualifier
