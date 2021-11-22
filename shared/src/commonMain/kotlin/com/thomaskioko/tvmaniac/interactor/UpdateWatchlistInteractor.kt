package com.thomaskioko.tvmaniac.interactor

import com.thomaskioko.tvmaniac.datasource.repository.tvshow.TvShowsRepository
import com.thomaskioko.tvmaniac.util.DomainResultState
import com.thomaskioko.tvmaniac.util.DomainResultState.Companion.loading
import com.thomaskioko.tvmaniac.util.DomainResultState.Companion.success
import com.thomaskioko.tvmaniac.util.Interactor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class UpdateWatchlistInteractor constructor(
    private val repository: TvShowsRepository,
) : Interactor<UpdateShowParams, Unit>() {

    override fun run(params: UpdateShowParams): Flow<DomainResultState<Unit>> = flow {
        emit(loading())

        repository.updateWatchlist(
            showId = params.showId,
            addToWatchList = params.addToWatchList
        )
        emit(success(Unit))
    }
        .catch {
            emit(DomainResultState.error(it))
        }
}

data class UpdateShowParams(
    val showId: Int,
    val addToWatchList: Boolean
)
