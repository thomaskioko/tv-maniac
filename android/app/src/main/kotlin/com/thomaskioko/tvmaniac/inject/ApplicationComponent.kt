package com.thomaskioko.tvmaniac.inject

import android.app.Application
import com.thomaskioko.tvmaniac.core.base.annotations.ApplicationScope
import com.thomaskioko.tvmaniac.initializers.AppInitializers
import com.thomaskioko.tvmaniac.shared.SharedComponent
import com.thomaskioko.tvmaniac.traktauth.implementation.TraktAuthComponent
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
@ApplicationScope
abstract class ApplicationComponent(
  @get:Provides val application: Application,
) : SharedComponent(), TraktAuthComponent {
  abstract val initializers: AppInitializers

  companion object
}
