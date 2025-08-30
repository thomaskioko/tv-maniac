package com.thomaskioko.tvmaniac.data.showdetails.implementation

import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsDao
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsRepository
import com.thomaskioko.tvmaniac.db.TvshowDetails
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.SHOW_DETAILS
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.get

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultShowDetailsRepository(
    private val showStore: ShowDetailsStore,
    private val dao: ShowDetailsDao,
    private val requestManagerRepository: RequestManagerRepository,
) : ShowDetailsRepository {

    override suspend fun fetchShowDetails(id: Long, forceRefresh: Boolean) {
        val statusIsNull = dao.getTvShow(id).status.isNullOrBlank()
        val isExpired = requestManagerRepository.isRequestExpired(
            entityId = id,
            requestType = SHOW_DETAILS.name,
            threshold = SHOW_DETAILS.duration,
        )

        when {
            forceRefresh || statusIsNull || isExpired -> showStore.fresh(id)
            else -> showStore.get(id)
        }
    }

    override fun observeShowDetails(id: Long): Flow<TvshowDetails> = dao.observeTvShows(id)
}
