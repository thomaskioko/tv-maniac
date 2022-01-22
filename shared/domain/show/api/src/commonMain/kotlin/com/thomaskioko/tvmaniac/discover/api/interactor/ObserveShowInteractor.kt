package com.thomaskioko.tvmaniac.discover.api.interactor

import com.thomaskioko.tvmaniac.datasource.cache.Show
import com.thomaskioko.tvmaniac.discover.api.model.ShowUiModel
import com.thomaskioko.tvmaniac.discover.api.repository.TvShowsRepository
import com.thomaskioko.tvmaniac.shared.core.FlowInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveShowInteractor constructor(
    private val repository: TvShowsRepository,
) : FlowInteractor<Int, ShowUiModel>() {

    override fun run(params: Int): Flow<ShowUiModel> = repository.observeShow(params)
        .map { it.data?.toTvShow() ?: ShowUiModel.EMPTY_SHOW }
}

fun Show.toTvShow(): ShowUiModel {
    return ShowUiModel(
        id = id.toInt(),
        title = title,
        overview = description,
        language = language,
        posterImageUrl = poster_image_url,
        backdropImageUrl = backdrop_image_url,
        votes = votes.toInt(),
        averageVotes = vote_average,
        genreIds = genre_ids,
        year = year,
        status = status,
        following = following,
        numberOfEpisodes = number_of_episodes?.toInt(),
        numberOfSeasons = number_of_episodes?.toInt()
    )
}
