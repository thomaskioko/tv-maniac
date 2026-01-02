package com.thomaskioko.tvmaniac.data.showdetails.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsDao
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.db.TvshowDetails
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultShowDetailsDao(
    database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : ShowDetailsDao {
    private val tvShowQueries = database.tvShowQueries

    override fun observeTvShows(id: Long): Flow<TvshowDetails> =
        tvShowQueries.tvshowDetails(Id(id)).asFlow().mapToOne(dispatchers.io)

    override fun getTvShow(id: Long): TvshowDetails =
        tvShowQueries.tvshowDetails(Id(id)).executeAsOne()

    override fun getTvShowOrNull(id: Long): TvshowDetails? =
        tvShowQueries.tvshowDetails(Id(id)).executeAsOneOrNull()

    override fun deleteTvShow(id: Long) {
        tvShowQueries.delete(Id(id))
    }
}
