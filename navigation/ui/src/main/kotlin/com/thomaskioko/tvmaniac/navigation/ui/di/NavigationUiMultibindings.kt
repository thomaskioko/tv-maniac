package com.thomaskioko.tvmaniac.navigation.ui.di

import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.ui.ScreenContent
import com.thomaskioko.tvmaniac.navigation.ui.SheetContent
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds

@ContributesTo(ActivityScope::class)
public interface NavigationUiMultibindings {
    @Multibinds
    public fun screenContents(): Set<ScreenContent>

    @Multibinds
    public fun sheetContents(): Set<SheetContent>
}
