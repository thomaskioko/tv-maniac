package com.thomaskioko.tvmaniac.data.showdetails.implementation

import com.thomaskioko.tvmaniac.data.showdetails.api.NetworksDao
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsDao
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsRepository
import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import me.tatarka.inject.annotations.Provides

interface ShowDetailsComponent {

    @ApplicationScope
    @Provides
    fun provideShowDetailsDao(
        bind: DefaultShowDetailsDao,
    ): ShowDetailsDao = bind

    @ApplicationScope
    @Provides
    fun provideNetworksDao(
        bind: DefaultNetworksDao,
    ): NetworksDao = bind

    @ApplicationScope
    @Provides
    fun provideShowDetailsRepository(
        bind: DefaultShowDetailsRepository,
    ): ShowDetailsRepository = bind
}
