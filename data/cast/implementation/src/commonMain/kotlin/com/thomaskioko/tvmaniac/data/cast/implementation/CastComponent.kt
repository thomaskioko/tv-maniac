package com.thomaskioko.tvmaniac.data.cast.implementation

import com.thomaskioko.tvmaniac.core.base.annotations.ApplicationScope
import com.thomaskioko.tvmaniac.data.cast.api.CastDao
import com.thomaskioko.tvmaniac.data.cast.api.CastRepository
import me.tatarka.inject.annotations.Provides

interface CastComponent {

  @ApplicationScope @Provides fun provideCastDao(bind: DefaultCastDao): CastDao = bind

  @ApplicationScope
  @Provides
  fun provideCastRepository(bind: DefaultCastRepository): CastRepository = bind
}
