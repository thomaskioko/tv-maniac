package com.thomaskioko.tvmaniac.seasondetails.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.EpisodesBySeasonId
import com.thomaskioko.tvmaniac.db.GetSeasonWithShowInfo
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.SeasonDetails
import com.thomaskioko.tvmaniac.db.SeasonImages
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsDao
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultSeasonDetailsDao(
    private val database: TvManiacDatabase,
    private val dispatcher: AppCoroutineDispatchers,
) : SeasonDetailsDao {

    private val seasonQueries
        get() = database.seasonsQueries

    private val episodesQueries
        get() = database.episodesQueries

    override fun observeSeasonDetails(
        showTraktId: Long,
        seasonNumber: Long,
    ): Flow<List<SeasonDetails>> =
        seasonQueries.seasonDetails(showTraktId = Id(showTraktId), seasonNumber = seasonNumber)
            .asFlow()
            .mapToList(dispatcher.io)

    override fun observeSeasonWithShowInfo(
        showTraktId: Long,
        seasonNumber: Long,
    ): Flow<GetSeasonWithShowInfo?> =
        seasonQueries.getSeasonWithShowInfo(showTraktId = Id(showTraktId), seasonNumber = seasonNumber)
            .asFlow()
            .mapToOneOrNull(dispatcher.io)

    override fun observeEpisodesBySeasonId(seasonId: Long): Flow<List<EpisodesBySeasonId>> =
        episodesQueries.episodesBySeasonId(seasonId = Id(seasonId))
            .asFlow()
            .mapToList(dispatcher.io)

    override fun delete(showTraktId: Long) {
        seasonQueries.delete(Id(showTraktId))
    }

    override fun deleteAll() {
        database.transaction { seasonQueries.deleteAll() }
    }

    override fun upsertSeasonImage(seasonId: Long, imageUrl: String) {
        database.transaction {
            database.seasonImagesQueries.upsert(
                season_id = Id(seasonId),
                image_url = imageUrl,
            )
        }
    }

    override fun fetchSeasonImages(id: Long): List<SeasonImages> =
        database.seasonImagesQueries.seasonImages(Id(id)).executeAsList()

    override fun observeSeasonImages(id: Long): Flow<List<SeasonImages>> =
        database.seasonImagesQueries.seasonImages(Id(id)).asFlow().mapToList(dispatcher.io)
}
