package com.thomaskioko.tvmaniac.search.implementation.di

import com.thomaskioko.tvmaniac.search.api.SearchRemoteDataSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds

@ContributesTo(AppScope::class)
public interface SearchMultibindings {

    @Multibinds(allowEmpty = true)
    public fun searchRemoteDataSources(): Set<SearchRemoteDataSource>
}
