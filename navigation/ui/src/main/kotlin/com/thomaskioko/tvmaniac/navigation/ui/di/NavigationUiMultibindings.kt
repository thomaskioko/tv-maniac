package com.thomaskioko.tvmaniac.navigation.ui.di

import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.ui.ScreenContent
import com.thomaskioko.tvmaniac.navigation.ui.SheetContent
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds

/**
 * Declares the two Compose-side multibinding sets feature `ui` modules contribute into.
 * Contributed at [ActivityScope] so each feature module ships its own renderers without touching
 * a central graph.
 *
 * - `Set<ScreenContent>`: matches one entry in the root navigation stack and renders the
 *   matching Compose screen.
 * - `Set<SheetContent>`: matches one entry in the modal sheet slot and renders the matching
 *   Compose sheet.
 */
@ContributesTo(ActivityScope::class)
public interface NavigationUiMultibindings {
    @Multibinds
    public fun screenContents(): Set<ScreenContent>

    @Multibinds
    public fun sheetContents(): Set<SheetContent>
}
