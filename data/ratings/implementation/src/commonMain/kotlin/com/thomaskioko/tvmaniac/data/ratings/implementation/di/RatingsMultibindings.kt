package com.thomaskioko.tvmaniac.data.ratings.implementation.di

import com.thomaskioko.tvmaniac.data.ratings.api.RatingsRemoteDataSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds

@ContributesTo(AppScope::class)
public interface RatingsMultibindings {
    @Multibinds(allowEmpty = true)
    public fun ratingsRemoteDataSources(): Set<RatingsRemoteDataSource>
}
