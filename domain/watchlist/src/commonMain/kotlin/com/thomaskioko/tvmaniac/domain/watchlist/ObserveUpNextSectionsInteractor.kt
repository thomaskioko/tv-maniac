package com.thomaskioko.tvmaniac.domain.watchlist

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.domain.watchlist.model.UpNextSections
import com.thomaskioko.tvmaniac.upnext.api.UpNextRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

@Inject
public class ObserveUpNextSectionsInteractor(
    private val upNextRepository: UpNextRepository,
    private val mapper: UpNextSectionsMapper,
) : SubjectInteractor<String, UpNextSections>() {

    override fun createObservable(params: String): Flow<UpNextSections> {
        return upNextRepository.observeNextEpisodesForWatchlist()
            .map { episodes -> mapper.map(episodes) }
            .map { sections -> sections.filterByQuery(params) }
    }
}
