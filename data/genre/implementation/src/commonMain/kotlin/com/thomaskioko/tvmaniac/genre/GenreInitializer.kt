package com.thomaskioko.tvmaniac.genre

import com.thomaskioko.tvmaniac.core.base.AppInitializer
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.InvokeError
import com.thomaskioko.tvmaniac.core.view.InvokeStarted
import com.thomaskioko.tvmaniac.core.view.InvokeSuccess
import com.thomaskioko.tvmaniac.domain.genre.GenresInteractor
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject
@ContributesBinding(AppScope::class, multibinding = true)
class GenreInitializer(
    private val interactor: GenresInteractor,
    private val logger: Logger,
    private val dispatchers: AppCoroutineDispatchers,
) : AppInitializer {
    override fun init() {
        GlobalScope.launch(dispatchers.main) {
            interactor(Unit).collect {
                when (it) {
                    is InvokeError -> logger.error("Error fetching genres", it.throwable)
                    InvokeStarted, InvokeSuccess -> {
                        // No-Op
                    }
                }
            }
        }
    }
}
