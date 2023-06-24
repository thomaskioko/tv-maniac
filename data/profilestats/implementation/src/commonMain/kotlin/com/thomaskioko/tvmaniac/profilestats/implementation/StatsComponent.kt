package com.thomaskioko.tvmaniac.profilestats.implementation

import com.thomaskioko.tvmaniac.profilestats.api.StatsDao
import com.thomaskioko.tvmaniac.profilestats.api.StatsRepository
import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import me.tatarka.inject.annotations.Provides

interface StatsComponent {

    @ApplicationScope
    @Provides
    fun provideStatsDao(bind: StatsDaoImpl): StatsDao = bind

    @ApplicationScope
    @Provides
    fun provideStatsRepository(bind: StatsRepositoryImpl): StatsRepository = bind
}
