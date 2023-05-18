package com.thomaskioko.tvmaniac.profile.implementation

import com.thomaskioko.tvmaniac.profile.api.ProfileDao
import com.thomaskioko.tvmaniac.profile.api.ProfileRepository
import com.thomaskioko.tvmaniac.profile.api.favorite.FavoriteListDao
import com.thomaskioko.tvmaniac.profile.implementation.favorite.FavoriteListDaoImpl
import me.tatarka.inject.annotations.Provides

interface ProfileComponent {

    @Provides
    fun provideProfileDao(bind: ProfileDaoImpl): ProfileDao = bind

    @Provides
    fun provideFavoriteListDao(bind: FavoriteListDaoImpl): FavoriteListDao = bind

    @Provides
    fun provideProfileRepository(bind: ProfileRepositoryImpl): ProfileRepository = bind

}
