package com.thomaskioko.tvmaniac.navigation.testing

import com.thomaskioko.tvmaniac.navigation.NavRoot
import kotlinx.serialization.Serializable

/**
 * Stand-in [NavRoot] used by test [com.thomaskioko.tvmaniac.navigation.Navigator] fakes when the
 * test does not supply an explicit initial root. Tests that observe `Navigator.activeRoot` should
 * pass a real root via the fake's constructor instead.
 */
@Serializable
public data object UnspecifiedNavRoot : NavRoot
