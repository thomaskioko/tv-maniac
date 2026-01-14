package com.thomaskioko.tvmaniac.data.cast.implementation

import com.thomaskioko.tvmaniac.data.cast.api.CastDao
import com.thomaskioko.tvmaniac.data.cast.api.CastRepository
import com.thomaskioko.tvmaniac.db.SeasonCast
import com.thomaskioko.tvmaniac.db.ShowCast
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.SHOW_CAST
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.get
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultCastRepository(
    private val dao: CastDao,
    private val showCastStore: ShowCastStore,
    private val requestManagerRepository: RequestManagerRepository,
) : CastRepository {

    override suspend fun fetchShowCast(showTraktId: Long, forceRefresh: Boolean) {
        val isEmpty = dao.observeShowCast(showTraktId).first().isEmpty()
        val isExpired = requestManagerRepository.isRequestExpired(
            entityId = showTraktId,
            requestType = SHOW_CAST.name,
            threshold = SHOW_CAST.duration,
        )

        when {
            forceRefresh || isEmpty || isExpired -> showCastStore.fresh(showTraktId)
            else -> showCastStore.get(showTraktId)
        }
    }

    override fun observeSeasonCast(seasonId: Long): Flow<List<SeasonCast>> =
        dao.observeSeasonCast(seasonId)

    override fun observeShowCast(showTraktId: Long): Flow<List<ShowCast>> =
        dao.observeShowCast(showTraktId)
}
