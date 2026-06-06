package com.thomaskioko.tvmaniac.data.trailers.implementation

import com.thomaskioko.tvmaniac.db.SelectByShowId
import com.thomaskioko.tvmaniac.util.api.AppUtils
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.get

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTrailerRepository(
    private val appUtils: AppUtils,
    private val trailerDao: TrailerDao,
    private val trailerStore: TrailerStore,
) : TrailerRepository {

    override fun isYoutubePlayerInstalled(): Flow<Boolean> = appUtils.isYoutubePlayerInstalled()

    override fun observeTrailers(showId: Long): Flow<List<SelectByShowId>> =
        trailerDao.observeTrailersByShowId(showId)

    override suspend fun fetchTrailers(showId: Long, forceRefresh: Boolean) {
        val isEmpty = trailerDao.getTrailersByShowId(showId).isEmpty()
        when {
            forceRefresh || isEmpty -> trailerStore.fresh(showId)
            else -> trailerStore.get(showId)
        }
    }
}
