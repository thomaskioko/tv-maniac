package com.thomaskioko.tvmaniac.data.user.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.user.api.UserStatsDao
import com.thomaskioko.tvmaniac.data.user.api.model.UserProfileStats
import com.thomaskioko.tvmaniac.data.user.api.model.UserWatchTime
import com.thomaskioko.tvmaniac.db.Stats
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultUserStatsDao(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : UserStatsDao {

    override fun observeUserStats(slug: String): Flow<Stats?> =
        database.statsQueries.select(slug)
            .asFlow()
            .mapToOneOrNull(dispatchers.databaseRead)

    override fun observeUserProfileStats(slug: String): Flow<UserProfileStats?> =
        database.statsQueries.select(slug)
            .asFlow()
            .mapToOneOrNull(dispatchers.databaseRead)
            .map { stats ->
                stats?.let {
                    val totalMinutes = it.minutes_watched.toInt()
                    val remainingAfterYears = totalMinutes % MINUTES_PER_YEAR
                    val remainingAfterDays = remainingAfterYears % MINUTES_PER_DAY
                    UserProfileStats(
                        showsWatched = it.shows_watched,
                        episodesWatched = it.episodes_watched,
                        userWatchTime = UserWatchTime(
                            years = totalMinutes / MINUTES_PER_YEAR,
                            days = remainingAfterYears / MINUTES_PER_DAY,
                            hours = remainingAfterDays / MINUTES_PER_HOUR,
                            minutes = remainingAfterDays % MINUTES_PER_HOUR,
                        ),
                    )
                }
            }

    private companion object {
        const val MINUTES_PER_HOUR = 60
        const val MINUTES_PER_DAY = 1440
        const val MINUTES_PER_YEAR = 525600
    }

    override fun getUserStats(slug: String): Stats? =
        database.statsQueries.select(slug).executeAsOneOrNull()

    override suspend fun upsertStats(
        slug: String,
        showsWatched: Long,
        episodesWatched: Long,
        minutesWatched: Long,
    ) {
        database.transaction {
            database.statsQueries.insertOrReplace(
                slug = slug,
                shows_watched = showsWatched,
                episodes_watched = episodesWatched,
                minutes_watched = minutesWatched,
            )
        }
    }

    override suspend fun deleteAll() {
        database.statsQueries.deleteAll()
    }
}
