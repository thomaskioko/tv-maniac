package com.thomaskioko.tvmaniac.profile.nav

import com.thomaskioko.tvmaniac.navigation.NavRoot
import kotlinx.serialization.Serializable

/**
 * Tab root for the Profile bottom-tab destination.
 *
 * Anchors the Profile tab's back stack. Contributed to `Set<NavRoot>` via
 * [com.thomaskioko.tvmaniac.profile.nav.di.ProfileRootBinding].
 */
@Serializable
public data object ProfileRoot : NavRoot
