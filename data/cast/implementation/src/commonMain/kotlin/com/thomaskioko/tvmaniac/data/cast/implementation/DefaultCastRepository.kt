package com.thomaskioko.tvmaniac.data.cast.implementation

import com.thomaskioko.tvmaniac.core.db.Season_cast
import com.thomaskioko.tvmaniac.data.cast.api.CastDao
import com.thomaskioko.tvmaniac.data.cast.api.CastRepository
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class DefaultCastRepository(
    private val dao: CastDao,
) : CastRepository {

    override fun fetchSeasonCast(seasonId: Long): List<Season_cast> =
        dao.fetchSeasonCast(seasonId)

    override fun observeSeasonCast(seasonId: Long): Flow<List<Season_cast>> =
        dao.observeSeasonCast(seasonId)
}
