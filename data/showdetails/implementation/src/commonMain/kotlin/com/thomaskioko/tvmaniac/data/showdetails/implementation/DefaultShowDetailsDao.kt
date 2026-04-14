package com.thomaskioko.tvmaniac.data.showdetails.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsDao
import com.thomaskioko.tvmaniac.db.Id
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

    override fun observeTvShowByTraktId(traktId: Long): Flow<TvshowDetails?> =
        tvShowQueries.tvshowDetails(Id(traktId)).asFlow().mapToOneOrNull(dispatchers.io)

    override fun getTvShow(traktId: Long): TvshowDetails =
        tvShowQueries.tvshowDetails(Id(traktId)).executeAsOne()

    override fun getTvShowOrNull(traktId: Long): TvshowDetails? =
        tvShowQueries.tvshowDetails(Id(traktId)).executeAsOneOrNull()

    override fun deleteTvShow(traktId: Long) {
        tvShowQueries.delete(Id(traktId))
    }
}
