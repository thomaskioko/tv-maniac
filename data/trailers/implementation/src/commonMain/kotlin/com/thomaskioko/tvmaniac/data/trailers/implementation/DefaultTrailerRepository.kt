package com.thomaskioko.tvmaniac.data.trailers.implementation

import com.thomaskioko.tvmaniac.db.Trailers
import com.thomaskioko.tvmaniac.util.api.AppUtils
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultTrailerRepository(
    private val appUtils: AppUtils,
    private val trailerDao: TrailerDao,
) : TrailerRepository {

    override fun isYoutubePlayerInstalled(): Flow<Boolean> = appUtils.isYoutubePlayerInstalled()

    override fun observeTrailers(id: Long): Flow<List<Trailers>> =
        trailerDao.observeTrailersById(id)
}
