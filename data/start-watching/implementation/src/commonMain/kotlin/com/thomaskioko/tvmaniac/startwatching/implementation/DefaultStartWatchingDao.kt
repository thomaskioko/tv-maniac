package com.thomaskioko.tvmaniac.startwatching.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.StartWatchingShows
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.startwatching.api.StartWatchingDao
import com.thomaskioko.tvmaniac.startwatching.api.StartWatchingShow
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultStartWatchingDao(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : StartWatchingDao {

    override fun observeStartWatchingShows(): Flow<List<StartWatchingShow>> =
        database.startWatchingQueries.startWatchingShows()
            .asFlow()
            .mapToList(dispatchers.databaseRead)
            .map { rows -> rows.map { it.toStartWatchingShow() } }
            .catch { emit(emptyList()) }
}

private fun StartWatchingShows.toStartWatchingShow(): StartWatchingShow =
    StartWatchingShow(
        traktId = show_trakt_id.id,
        tmdbId = show_tmdb_id.id,
        title = title,
        posterPath = poster_path,
        year = year,
        inLibrary = in_library == 1L,
    )
