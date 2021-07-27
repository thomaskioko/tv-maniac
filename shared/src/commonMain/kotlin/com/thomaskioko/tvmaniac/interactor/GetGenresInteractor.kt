package com.thomaskioko.tvmaniac.interactor

import com.thomaskioko.tvmaniac.datasource.repository.genre.GenreRepository
import com.thomaskioko.tvmaniac.presentation.model.GenreModel
import com.thomaskioko.tvmaniac.util.DomainResultState
import com.thomaskioko.tvmaniac.util.DomainResultState.Companion.error
import com.thomaskioko.tvmaniac.util.DomainResultState.Companion.loading
import com.thomaskioko.tvmaniac.util.DomainResultState.Companion.success
import com.thomaskioko.tvmaniac.util.Interactor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetGenresInteractor constructor(
    private val repository: GenreRepository,
) : Interactor<Unit, List<GenreModel>>() {

    override fun run(params: Unit): Flow<DomainResultState<List<GenreModel>>> = flow {
        emit(loading())

        emit(success(repository.getGenres()))
    }
        .catch { emit(error(it)) }
}