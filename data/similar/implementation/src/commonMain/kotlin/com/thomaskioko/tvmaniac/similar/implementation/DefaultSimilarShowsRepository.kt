package com.thomaskioko.tvmaniac.similar.implementation

import com.thomaskioko.tvmaniac.db.SimilarShows
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.SIMILAR_SHOWS
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsDao
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import com.thomaskioko.tvmaniac.tmdb.api.DEFAULT_API_PAGE
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.get

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultSimilarShowsRepository(
    private val store: SimilarShowStore,
    private val dao: SimilarShowsDao,
    private val requestManagerRepository: RequestManagerRepository,
) : SimilarShowsRepository {

    override suspend fun fetchSimilarShows(id: Long, forceRefresh: Boolean) {
        val param = SimilarParams(showId = id, page = DEFAULT_API_PAGE)
        val isEmpty = dao.observeSimilarShows(id).first().isEmpty()
        val isExpired = requestManagerRepository.isRequestExpired(
            entityId = id,
            requestType = SIMILAR_SHOWS.name,
            threshold = SIMILAR_SHOWS.duration,
        )

        when {
            forceRefresh || isEmpty || isExpired -> store.fresh(param)
            else -> store.get(param)
        }
    }

    override fun observeSimilarShows(id: Long): Flow<List<SimilarShows>> = dao.observeSimilarShows(id)
}
