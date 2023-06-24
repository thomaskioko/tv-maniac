package com.thomaskioko.tvmaniac.profile.implementation

import com.thomaskioko.tvmaniac.profile.api.ProfileDao
import com.thomaskioko.tvmaniac.profile.api.ProfileRepository
import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import me.tatarka.inject.annotations.Provides

interface ProfileComponent {

    @ApplicationScope
    @Provides
    fun provideProfileDao(bind: ProfileDaoImpl): ProfileDao = bind

    @ApplicationScope
    @Provides
    fun provideProfileRepository(bind: ProfileRepositoryImpl): ProfileRepository = bind
}
