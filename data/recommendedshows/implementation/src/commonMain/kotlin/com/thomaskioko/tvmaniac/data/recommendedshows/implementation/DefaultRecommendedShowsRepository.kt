package com.thomaskioko.tvmaniac.data.recommendedshows.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.recommendedshows.api.RecommendedShowsDao
import com.thomaskioko.tvmaniac.data.recommendedshows.api.RecommendedShowsParams
import com.thomaskioko.tvmaniac.data.recommendedshows.api.RecommendedShowsRepository
import com.thomaskioko.tvmaniac.db.RecommendedShows
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.RECOMMENDED_SHOWS
import com.thomaskioko.tvmaniac.tmdb.api.DEFAULT_API_PAGE
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.get

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultRecommendedShowsRepository(
    private val store: RecommendedShowsStore,
    private val dao: RecommendedShowsDao,
    private val dispatchers: AppCoroutineDispatchers,
    private val requestManagerRepository: RequestManagerRepository,
) : RecommendedShowsRepository {
    override suspend fun fetchRecommendedShows(
        id: Long,
        forceRefresh: Boolean,
    ) {
        val key = RecommendedShowsParams(showId = id, page = DEFAULT_API_PAGE)
        val isEmpty = dao.observeRecommendedShows(id).first().isEmpty()
        val isExpired = requestManagerRepository.isRequestExpired(
            entityId = id,
            requestType = RECOMMENDED_SHOWS.name,
            threshold = RECOMMENDED_SHOWS.duration,
        )

        when {
            forceRefresh || isEmpty || isExpired -> store.fresh(key)
            else -> store.get(key)
        }
    }

    override fun observeRecommendedShows(
        id: Long,
    ): Flow<List<RecommendedShows>> {
        return dao.observeRecommendedShows(id)
            .flowOn(dispatchers.io)
    }
}
