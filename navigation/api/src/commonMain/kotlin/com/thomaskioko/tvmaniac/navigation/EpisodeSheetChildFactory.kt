package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.root.model.EpisodeSheetConfig

/**
 * Creates the [SheetChild] shown in the root modal sheet slot for a given [EpisodeSheetConfig].
 *
 * The episode sheet is the one sheet surface that can be triggered from any screen, so it is
 * modeled as a standalone factory rather than a [NavDestination] multibinding. The root presenter
 * calls [createChild] when the episode sheet slot is active and renders the returned
 * [SheetChild] in the sheet overlay.
 */
public interface EpisodeSheetChildFactory {
    /**
     * Builds the sheet's presenter tree for [config] under the supplied [componentContext].
     */
    public fun createChild(config: EpisodeSheetConfig, componentContext: ComponentContext): SheetChild
}
