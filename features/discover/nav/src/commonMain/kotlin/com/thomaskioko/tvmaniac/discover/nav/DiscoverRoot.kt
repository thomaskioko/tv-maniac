package com.thomaskioko.tvmaniac.discover.nav

import com.thomaskioko.tvmaniac.navigation.NavRoot
import kotlinx.serialization.Serializable

/**
 * Tab root for the Discover bottom-tab destination.
 *
 * Anchors the Discover tab's back stack: the tab body renders when this is the only entry, and
 * pushed routes accumulate above it. Contributed to `Set<NavRoot>` via [com.thomaskioko.tvmaniac.discover.nav.di.DiscoverRootBinding].
 */
@Serializable
public data object DiscoverRoot : NavRoot
