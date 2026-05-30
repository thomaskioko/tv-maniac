package com.thomaskioko.tvmaniac.domain.continuewatching

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.upnext.api.UpNextRepository
import com.thomaskioko.tvmaniac.upnext.api.model.CompletedShow
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Inject
public class ObserveCompletedShowsInteractor(
    private val upNextRepository: UpNextRepository,
) : SubjectInteractor<ObserveCompletedShowsInteractor.Param, List<CompletedShow>>() {

    override fun createObservable(params: Param): Flow<List<CompletedShow>> =
        upNextRepository.observeCompletedShows()
            .map { shows -> shows.take(params.limit) }

    public data class Param(
        val limit: Int = DEFAULT_LIMIT,
    )

    public companion object {
        public const val DEFAULT_LIMIT: Int = 20
    }
}
