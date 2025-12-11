package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.episodes.api.EpisodesDao
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import com.thomaskioko.tvmaniac.db.Episode as EpisodeCache

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultEpisodesDao(
    private val database: TvManiacDatabase,
) : EpisodesDao {

    private val episodeQueries
        get() = database.episodesQueries

    override fun insert(entity: EpisodeCache) {
        database.transaction {
            episodeQueries.upsert(
                id = entity.id,
                season_id = entity.season_id,
                title = entity.title,
                overview = entity.overview,
                runtime = entity.runtime,
                episode_number = entity.episode_number,
                image_url = entity.image_url,
                show_id = entity.show_id,
                vote_count = entity.vote_count,
                vote_average = entity.vote_average,
                air_date = entity.air_date,
                trakt_id = entity.trakt_id,
            )
        }
    }

    override fun insert(list: List<EpisodeCache>) {
        list.map { insert(it) }
    }

    override fun delete(id: Long) {
        episodeQueries.delete(Id(id))
    }

    override fun deleteAll() {
        database.transaction { episodeQueries.deleteAll() }
    }
}
