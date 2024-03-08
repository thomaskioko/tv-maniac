package com.thomaskioko.tvmaniac.traktauth.implementation

import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import me.tatarka.inject.annotations.Provides

interface TraktAuthManagerComponent {

  @ActivityScope
  @Provides
  fun provideTraktAuthManager(bind: DefaultTraktAuthManager): TraktAuthManager = bind
}
