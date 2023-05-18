package com.thomaskioko.tvmaniac.profilestats.implementation

import com.thomaskioko.tvmaniac.profilestats.api.StatsDao
import com.thomaskioko.tvmaniac.profilestats.api.StatsRepository
import me.tatarka.inject.annotations.Provides

interface StatsComponent {

    @Provides
    fun provideStatsDao(bind: StatsDaoImpl): StatsDao = bind


    @Provides
    fun provideStatsRepository(bind: StatsRepositoryImpl): StatsRepository = bind
}
