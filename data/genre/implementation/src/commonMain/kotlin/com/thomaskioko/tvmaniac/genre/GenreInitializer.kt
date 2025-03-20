package com.thomaskioko.tvmaniac.genre

import com.thomaskioko.tvmaniac.core.base.AppInitializer
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject
@ContributesBinding(AppScope::class, multibinding = true)
class GenreInitializer(
  private val genreRepository: GenreRepository,
  private val coroutineScope: AppCoroutineScope,
) : AppInitializer {
  override fun init() {
    // Delay initialization to avoid database contention during app startup
    coroutineScope.io.launch {
      delay(1000)
      genreRepository.observeGenresWithShows().collect()
    }
  }
}
