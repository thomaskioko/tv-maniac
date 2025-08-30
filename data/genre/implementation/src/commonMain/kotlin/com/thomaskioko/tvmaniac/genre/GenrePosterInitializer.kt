package com.thomaskioko.tvmaniac.genre

import com.thomaskioko.tvmaniac.core.base.AppInitializer
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Inject
class GenrePosterInitializer(
    private val genreRepository: GenreRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : AppInitializer {
    override fun init() {
        GlobalScope.launch(dispatchers.main) {
            genreRepository.observeGenrePosters()
        }
    }
}
