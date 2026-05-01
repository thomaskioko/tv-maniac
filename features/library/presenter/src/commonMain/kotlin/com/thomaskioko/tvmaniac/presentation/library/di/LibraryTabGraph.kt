package com.thomaskioko.tvmaniac.presentation.library.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.home.nav.HomeRoute
import com.thomaskioko.tvmaniac.library.nav.LibraryRoot
import com.thomaskioko.tvmaniac.presentation.library.LibraryPresenter
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

@GraphExtension(LibraryRoot::class)
public interface LibraryTabGraph {
    public val libraryPresenter: LibraryPresenter

    @ContributesTo(HomeRoute::class)
    @GraphExtension.Factory
    public interface Factory {
        public fun createLibraryTabGraph(@Provides componentContext: ComponentContext): LibraryTabGraph
    }
}
