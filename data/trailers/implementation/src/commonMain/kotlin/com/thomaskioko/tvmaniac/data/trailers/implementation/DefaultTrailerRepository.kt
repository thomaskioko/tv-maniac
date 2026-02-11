package com.thomaskioko.tvmaniac.data.trailers.implementation

import com.thomaskioko.tvmaniac.db.SelectByShowTraktId
import com.thomaskioko.tvmaniac.util.api.AppUtils
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.get
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTrailerRepository(
    private val appUtils: AppUtils,
    private val trailerDao: TrailerDao,
    private val trailerStore: TrailerStore,
) : TrailerRepository {

    override fun isYoutubePlayerInstalled(): Flow<Boolean> = appUtils.isYoutubePlayerInstalled()

    override fun observeTrailers(traktId: Long): Flow<List<SelectByShowTraktId>> =
        trailerDao.observeTrailersByShowTraktId(traktId)

    override suspend fun fetchTrailers(traktId: Long, forceRefresh: Boolean) {
        val isEmpty = trailerDao.getTrailersByShowTraktId(traktId).isEmpty()
        when {
            forceRefresh || isEmpty -> trailerStore.fresh(traktId)
            else -> trailerStore.get(traktId)
        }
    }
}
