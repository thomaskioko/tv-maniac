package com.thomaskioko.tvmaniac.domain.startwatching

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.startwatching.api.StartWatchingRepository
import com.thomaskioko.tvmaniac.startwatching.api.StartWatchingShow
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
public class ObserveStartWatchingInteractor(
    private val repository: StartWatchingRepository,
) : SubjectInteractor<Unit, List<StartWatchingShow>>() {

    override fun createObservable(params: Unit): Flow<List<StartWatchingShow>> =
        repository.observeStartWatching()
}
