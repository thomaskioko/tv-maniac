package com.thomaskioko.tvmaniac.data.ratings.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.ratings.api.ProviderMetaDao
import com.thomaskioko.tvmaniac.data.ratings.api.ProviderRating
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Provider
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultProviderMetaDao(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : ProviderMetaDao {

    private val queries = database.tvshowProviderMetaQueries

    override fun upsertProviderRating(showId: Long, provider: Provider, rating: Double, voteCount: Long, lastSyncedAt: Long) {
        queries.upsertProviderRating(
            showId = Id(showId),
            provider = provider,
            rating = rating,
            voteCount = voteCount,
            lastSyncedAt = lastSyncedAt,
        )
    }

    override fun observeProviderRating(showId: Long, provider: Provider): Flow<ProviderRating?> =
        queries.providerRating(Id(showId), provider)
            .asFlow()
            .mapToOneOrNull(dispatchers.io)
            .map { row ->
                val rating = row?.rating
                val voteCount = row?.vote_count
                if (rating != null && voteCount != null) {
                    ProviderRating(showId = showId, rating = rating, voteCount = voteCount)
                } else {
                    null
                }
            }

    override fun clearAll() {
        queries.deleteAll()
    }
}
