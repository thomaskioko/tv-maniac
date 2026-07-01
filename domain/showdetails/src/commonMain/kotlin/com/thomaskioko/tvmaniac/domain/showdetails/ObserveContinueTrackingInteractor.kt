package com.thomaskioko.tvmaniac.domain.showdetails

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasondetails.api.model.EpisodeDetails
import dev.zacsweers.metro.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

@Inject
public class ObserveContinueTrackingInteractor(
    private val seasonDetailsRepository: SeasonDetailsRepository,
    private val followedShowsRepository: FollowedShowsRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : SubjectInteractor<Long, ImmutableList<EpisodeDetails>>() {

    override fun createObservable(params: Long): Flow<ImmutableList<EpisodeDetails>> =
        combine(
            seasonDetailsRepository.observeContinueTrackingEpisodes(params)
                .map { it?.episodes ?: persistentListOf() },
            followedShowsRepository.observeIsFollowed(params),
        ) { episodes, isFollowed ->
            if (isFollowed) episodes else persistentListOf()
        }.flowOn(dispatchers.io)
}
