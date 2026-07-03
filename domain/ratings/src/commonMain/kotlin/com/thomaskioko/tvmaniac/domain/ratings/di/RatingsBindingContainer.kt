package com.thomaskioko.tvmaniac.domain.ratings.di

import com.thomaskioko.tvmaniac.core.base.AsyncInitializers
import com.thomaskioko.tvmaniac.core.base.Initializer
import com.thomaskioko.tvmaniac.domain.ratings.RatingsSyncInitializer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@BindingContainer
@ContributesTo(AppScope::class)
public interface RatingsBindingContainer {
    public companion object {
        @Provides
        @IntoSet
        @AsyncInitializers
        public fun provideRatingsSyncInitializer(
            bind: RatingsSyncInitializer,
        ): Initializer = Initializer { bind.init() }
    }
}
