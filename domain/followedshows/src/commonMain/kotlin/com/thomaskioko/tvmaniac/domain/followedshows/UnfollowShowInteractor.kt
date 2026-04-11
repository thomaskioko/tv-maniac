package com.thomaskioko.tvmaniac.domain.followedshows

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsRepository
import dev.zacsweers.metro.Inject

@Inject
public class UnfollowShowInteractor(
    private val followedShowsRepository: FollowedShowsRepository,
) : Interactor<Long>() {

    override suspend fun doWork(params: Long) {
        followedShowsRepository.removeFollowedShow(params)
    }
}
