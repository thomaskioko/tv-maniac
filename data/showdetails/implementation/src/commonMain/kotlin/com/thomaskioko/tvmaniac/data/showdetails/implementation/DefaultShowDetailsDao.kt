package com.thomaskioko.tvmaniac.data.showdetails.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsDao
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.db.TvshowDetails
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultShowDetailsDao(
    database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : ShowDetailsDao {
    private val tvShowQueries = database.tvShowQueries

    override fun observeTvShowByTraktId(showId: Long): Flow<TvshowDetails?> =
        tvShowQueries.tvshowDetails(showId).asFlow().mapToOneOrNull(dispatchers.io)

    override fun getTvShow(showId: Long): TvshowDetails =
        tvShowQueries.tvshowDetails(showId).executeAsOne()

    override fun getTvShowOrNull(showId: Long): TvshowDetails? =
        tvShowQueries.tvshowDetails(showId).executeAsOneOrNull()

    override fun deleteTvShow(showId: Long) {
        tvShowQueries.delete(showId)
    }
}
