package com.thomaskioko.tvmaniac.traktauth.implementation

import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import me.tatarka.inject.annotations.Provides

interface TraktAuthenticationComponent {

  @ApplicationScope
  @Provides
  fun provideTraktAuthRepository(bind: TraktAuthRepositoryImpl): TraktAuthRepository = bind
}
