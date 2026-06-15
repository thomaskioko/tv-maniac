package com.thomaskioko.tvmaniac.domain.calendar

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsDao
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Inject
public class ObserveFollowedShowIdsInteractor(
    private val followedShowsDao: FollowedShowsDao,
) : SubjectInteractor<Unit, Set<Long>>() {

    override fun createObservable(params: Unit): Flow<Set<Long>> =
        followedShowsDao.entriesObservable()
            .map { entries -> entries.mapNotNull { it.tmdbId }.toSet() }
            .distinctUntilChanged()
}
