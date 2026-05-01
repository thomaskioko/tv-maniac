package com.thomaskioko.tvmaniac.progress.nav

import com.thomaskioko.tvmaniac.navigation.NavRoot
import kotlinx.serialization.Serializable

/**
 * Tab root for the Progress bottom-tab destination.
 *
 * Anchors the Progress tab's back stack. Contributed to `Set<NavRoot>` via
 * [com.thomaskioko.tvmaniac.progress.nav.di.ProgressRootBinding].
 */
@Serializable
public data object ProgressRoot : NavRoot
