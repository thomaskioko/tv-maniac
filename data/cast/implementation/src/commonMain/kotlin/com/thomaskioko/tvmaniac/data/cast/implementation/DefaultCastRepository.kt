package com.thomaskioko.tvmaniac.data.cast.implementation

import com.thomaskioko.tvmaniac.data.cast.api.CastDao
import com.thomaskioko.tvmaniac.data.cast.api.CastRepository
import com.thomaskioko.tvmaniac.db.SeasonCast
import com.thomaskioko.tvmaniac.db.ShowCast
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultCastRepository(
    private val dao: CastDao,
) : CastRepository {

    override fun observeSeasonCast(seasonId: Long): Flow<List<SeasonCast>> =
        dao.observeSeasonCast(seasonId)

    override fun observeShowCast(showId: Long): Flow<List<ShowCast>> = dao.observeShowCast(showId)
}
