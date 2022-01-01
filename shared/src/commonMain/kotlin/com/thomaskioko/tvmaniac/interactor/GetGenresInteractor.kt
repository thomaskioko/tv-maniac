package com.thomaskioko.tvmaniac.interactor

import com.thomaskioko.tvmaniac.core.usecase.FlowInteractor
import com.thomaskioko.tvmaniac.datasource.mapper.toGenreModelList
import com.thomaskioko.tvmaniac.datasource.repository.genre.GenreRepository
import com.thomaskioko.tvmaniac.presentation.model.GenreUIModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetGenresInteractor constructor(
    private val repository: GenreRepository,
) : FlowInteractor<Unit, List<GenreUIModel>>() {

    override fun run(params: Unit): Flow<List<GenreUIModel>> = repository.observeGenres()
        .map { it.data?.toGenreModelList() ?: emptyList() }
}
