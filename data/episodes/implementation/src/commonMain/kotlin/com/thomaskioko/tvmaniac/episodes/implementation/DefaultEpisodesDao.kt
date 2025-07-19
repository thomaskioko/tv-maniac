package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.episodes.api.EpisodesDao
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import com.thomaskioko.tvmaniac.db.Episode as EpisodeCache

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultEpisodesDao(
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
