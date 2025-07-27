package com.thomaskioko.tvmaniac.seasondetails.implementation

import com.thomaskioko.tvmaniac.db.Season_images
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsDao
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasondetails.api.model.SeasonDetailsWithEpisodes
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
class DefaultSeasonDetailsRepository(
    private val store: SeasonDetailsStore,
    private val dao: SeasonDetailsDao,
) : SeasonDetailsRepository {
    override suspend fun fetchSeasonDetails(
        param: SeasonDetailsParam,
        forceRefresh: Boolean,
    ) {
        val isEmpty = dao.observeSeasonEpisodeDetails(param.showId, param.seasonNumber).first().episodes.isEmpty()
        when {
            forceRefresh || isEmpty -> store.fresh(param)
            else -> store.get(param)
        }
    }

    override fun observeSeasonDetails(
        param: SeasonDetailsParam,
    ): Flow<SeasonDetailsWithEpisodes> = dao.observeSeasonEpisodeDetails(param.showId, param.seasonNumber)

    override fun observeSeasonImages(id: Long): Flow<List<Season_images>> = dao.observeSeasonImages(id)
}
