package com.thomaskioko.tvmaniac.traktauth.implementation

import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import com.thomaskioko.tvmaniac.util.scope.ActivityScope
import me.tatarka.inject.annotations.Provides

interface TraktAuthManagerComponent {

  @ActivityScope
  @Provides
  fun provideTraktAuthManager(bind: DefaultTraktAuthManager): TraktAuthManager = bind
}
