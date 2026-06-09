package com.thomaskioko.tvmaniac.domain.showdetails

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsRepository
import com.thomaskioko.tvmaniac.domain.showdetails.model.ShowDetails
import com.thomaskioko.tvmaniac.util.api.FormatterUtil
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

@Inject
public class ObservableShowDetailsInteractor(
    private val showDetailsRepository: ShowDetailsRepository,
    private val formatterUtil: FormatterUtil,
    private val dispatchers: AppCoroutineDispatchers,
) : SubjectInteractor<Long, ShowDetails>() {

    override fun createObservable(params: Long): Flow<ShowDetails> =
        showDetailsRepository.observeShowDetails(params)
            .map { row ->
                ShowDetails(
                    showId = row.trakt_id,
                    tmdbId = row.tmdb_id.id,
                    title = row.name,
                    overview = row.overview,
                    language = row.language,
                    posterImageUrl = row.poster_path,
                    backdropImageUrl = row.backdrop_path,
                    votes = row.vote_count,
                    rating = formatterUtil.formatDouble(row.ratings, 1),
                    year = row.year ?: "",
                    status = row.status,
                    isInLibrary = row.in_library == 1L,
                    genres = row.genres ?: emptyList(),
                )
            }
            .flowOn(dispatchers.io)
}
