package com.thomaskioko.tvmaniac.traktauth.implementation

import com.thomaskioko.tvmaniac.core.base.annotations.ApplicationScope
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import me.tatarka.inject.annotations.Provides

interface TraktAuthenticationComponent {

  @ApplicationScope
  @Provides
  fun provideTraktAuthRepository(bind: TraktAuthRepositoryImpl): TraktAuthRepository = bind
}
