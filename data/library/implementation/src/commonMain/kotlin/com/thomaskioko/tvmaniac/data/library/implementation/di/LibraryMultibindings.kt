package com.thomaskioko.tvmaniac.data.library.implementation.di

import com.thomaskioko.tvmaniac.data.library.LibraryRemoteDataSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds

@ContributesTo(AppScope::class)
public interface LibraryMultibindings {

    @Multibinds(allowEmpty = true)
    public fun libraryRemoteDataSources(): Set<LibraryRemoteDataSource>
}
