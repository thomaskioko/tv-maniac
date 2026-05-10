package com.thomaskioko.tvmaniac.library.nav

import com.thomaskioko.tvmaniac.navigation.NavRoot
import kotlinx.serialization.Serializable

/**
 * Tab root for the Library bottom-tab destination.
 *
 * Anchors the Library tab's back stack. Contributed to `Set<NavRoot>` via
 * [com.thomaskioko.tvmaniac.library.nav.di.LibraryRootBinding].
 */
@Serializable
public data object LibraryRoot : NavRoot
