package com.thomaskioko.tvmaniac.data.featuredshows.implementation

import com.thomaskioko.tvmaniac.data.featuredshows.api.FeaturedShowsDao
import com.thomaskioko.tvmaniac.data.featuredshows.api.FeaturedShowsRepository
import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import me.tatarka.inject.annotations.Provides

interface FeaturedShowsComponent {

    @ApplicationScope
    @Provides
    fun provideFeaturedShowsDao(
        bind: DefaultFeaturedShowsDao,
    ): FeaturedShowsDao = bind

    @ApplicationScope
    @Provides
    fun provideFeaturedShowsRepository(
        bind: DefaultFeaturedShowsRepository,
    ): FeaturedShowsRepository = bind
}
