package com.thomaskioko.tvmaniac.genre

import com.thomaskioko.tvmaniac.core.base.AppInitializer
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject
@ContributesBinding(AppScope::class, multibinding = true)
public class GenrePosterInitializer(
    private val genreRepository: GenreRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : AppInitializer {
    override fun init() {
        GlobalScope.launch(dispatchers.main) {
            genreRepository.observeGenrePosters()
        }
    }
}
