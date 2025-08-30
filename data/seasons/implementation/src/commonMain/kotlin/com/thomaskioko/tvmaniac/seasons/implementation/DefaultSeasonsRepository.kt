package com.thomaskioko.tvmaniac.seasons.implementation

import com.thomaskioko.tvmaniac.db.ShowSeasons
import com.thomaskioko.tvmaniac.seasons.api.SeasonsDao
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultSeasonsRepository(
    private val seasonsDao: SeasonsDao,
) : SeasonsRepository {

    override fun observeSeasonsByShowId(id: Long): Flow<List<ShowSeasons>> =
        seasonsDao.observeSeasonsByShowId(id)
}
