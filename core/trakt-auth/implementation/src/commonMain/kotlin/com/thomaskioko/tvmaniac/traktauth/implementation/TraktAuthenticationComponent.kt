package com.thomaskioko.tvmaniac.traktauth.implementation

import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import me.tatarka.inject.annotations.Provides

interface TraktAuthenticationComponent {

    @Provides
    fun provideTraktAuthRepository(bind: TraktAuthRepositoryImpl): TraktAuthRepository = bind
}
