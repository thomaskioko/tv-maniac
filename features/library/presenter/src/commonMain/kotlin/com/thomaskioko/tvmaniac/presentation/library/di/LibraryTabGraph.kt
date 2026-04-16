package com.thomaskioko.tvmaniac.presentation.library.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.home.nav.scope.HomeScreenScope
import com.thomaskioko.tvmaniac.library.nav.LibraryTabScope
import com.thomaskioko.tvmaniac.presentation.library.LibraryPresenter
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

@GraphExtension(LibraryTabScope::class)
public interface LibraryTabGraph {
    public val libraryPresenter: LibraryPresenter

    @ContributesTo(HomeScreenScope::class)
    @GraphExtension.Factory
    public interface Factory {
        public fun createLibraryTabGraph(
            @Provides componentContext: ComponentContext,
        ): LibraryTabGraph
    }
}
