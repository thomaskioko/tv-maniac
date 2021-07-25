package com.thomaskioko.tvmaniac.interactor

import com.thomaskioko.tvmaniac.datasource.cache.model.EpisodeEntity
import com.thomaskioko.tvmaniac.datasource.repository.episode.EpisodeRepository
import com.thomaskioko.tvmaniac.util.DomainResultState
import com.thomaskioko.tvmaniac.util.DomainResultState.Companion.error
import com.thomaskioko.tvmaniac.util.DomainResultState.Companion.loading
import com.thomaskioko.tvmaniac.util.DomainResultState.Companion.success
import com.thomaskioko.tvmaniac.util.Interactor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class EpisodesInteractor constructor(
    private val repository: EpisodeRepository,
) : Interactor<EpisodeQuery, List<EpisodeEntity>>() {

    override fun run(params: EpisodeQuery): Flow<DomainResultState<List<EpisodeEntity>>> = flow {
        emit(loading())

        val result = repository.getEpisodesBySeasonId(
            tvShowId = params.tvShowId,
            seasonId = params.seasonId,
            seasonNumber = params.seasonNumber
        ).sortedBy { it.episodeNumber }

        emit(success(result))
    }
        .catch { emit(error(it)) }
}

data class EpisodeQuery(
    val tvShowId: Int,
    val seasonId: Int,
    val seasonNumber: Int
)