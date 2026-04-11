package com.thomaskioko.tvmaniac.similar.implementation

import com.thomaskioko.tvmaniac.db.SimilarShows
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsDao
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.get

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultSimilarShowsRepository(
    private val store: SimilarShowStore,
    private val dao: SimilarShowsDao,
) : SimilarShowsRepository {

    override suspend fun fetchSimilarShows(traktId: Long, forceRefresh: Boolean) {
        val param = SimilarParams(showTraktId = traktId)

        when {
            forceRefresh -> store.fresh(param)
            else -> store.get(param)
        }
    }

    override fun observeSimilarShows(id: Long): Flow<List<SimilarShows>> = dao.observeSimilarShows(id)
}
