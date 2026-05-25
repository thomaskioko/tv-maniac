package com.thomaskioko.tvmaniac.startwatching.implementation

import com.thomaskioko.tvmaniac.startwatching.api.StartWatchingDao
import com.thomaskioko.tvmaniac.startwatching.api.StartWatchingRepository
import com.thomaskioko.tvmaniac.startwatching.api.StartWatchingShow
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultStartWatchingRepository(
    private val dao: StartWatchingDao,
) : StartWatchingRepository {

    override fun observeStartWatching(): Flow<List<StartWatchingShow>> = dao.observeStartWatchingShows()
}
