package com.thomaskioko.tvmaniac.genre.api

import com.thomaskioko.tvmaniac.shared.core.FlowInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetGenresInteractor constructor(
    private val repository: GenreRepository,
) : FlowInteractor<Unit, List<GenreUIModel>>() {

    override fun run(params: Unit): Flow<List<GenreUIModel>> = repository.observeGenres()
        .map { it.data?.toGenreModelList() ?: emptyList() }
}
