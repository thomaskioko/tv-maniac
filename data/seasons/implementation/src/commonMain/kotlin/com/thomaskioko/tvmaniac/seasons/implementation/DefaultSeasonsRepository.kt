package com.thomaskioko.tvmaniac.seasons.implementation

import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.db.ShowSeasons
import com.thomaskioko.tvmaniac.seasons.api.FollowedShowSeason
import com.thomaskioko.tvmaniac.seasons.api.SeasonsDao
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@OptIn(ExperimentalCoroutinesApi::class)
@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultSeasonsRepository(
    private val seasonsDao: SeasonsDao,
    private val datastoreRepository: DatastoreRepository,
) : SeasonsRepository {

    override fun observeSeasonsByShowId(id: Long): Flow<List<ShowSeasons>> {
        return datastoreRepository.observeIncludeSpecials()
            .flatMapLatest { includeSpecials ->
                seasonsDao.observeSeasonsByShowTraktId(id, includeSpecials)
            }
    }

    override fun getSeasonsByShowId(id: Long, includeSpecials: Boolean): List<ShowSeasons> {
        return seasonsDao.fetchShowSeasons(id, includeSpecials)
    }

    override suspend fun getLatestSeasonsForFollowedShows(): List<FollowedShowSeason> {
        return seasonsDao.getLatestSeasonPerFollowedShow().map { row ->
            FollowedShowSeason(
                showTraktId = row.show_trakt_id.id,
                seasonId = row.season_id.id,
                seasonNumber = row.season_number,
            )
        }
    }
}
