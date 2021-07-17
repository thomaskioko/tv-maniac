package com.thomaskioko.tvmaniac.datasource.cache.db

import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase
import com.thomaskioko.tvmaniac.datasource.cache.model.TvShowsEntity
import com.thomaskioko.tvmaniac.datasource.mapper.toTvShowsEntity
import com.thomaskioko.tvmaniac.datasource.mapper.toTvShowsEntityList

class TvShowCacheImpl(
    private val database: TvManiacDatabase
) : TvShowCache {

    override fun insert(entity: TvShowsEntity) {
        database.tvShowQueries.transaction {
            database.tvShowQueries.insertOrReplace(
                id = entity.id.toLong(),
                title = entity.title,
                description = entity.description,
                language = entity.language,
                image_url = entity.imageUrl,
                votes = entity.votes.toLong(),
                vote_average = entity.averageVotes,
                genre_ids = entity.genreIds,
                show_category = entity.showCategory
            )
        }
    }

    override fun insert(entityList: List<TvShowsEntity>) {
        entityList.forEach { insert(it) }
    }

    override fun getTvShow(showId: Int): TvShowsEntity {
        return database.tvShowQueries.selectByShowId(
            id = showId.toLong()
        ).executeAsOne().toTvShowsEntity()
    }


    override fun getTvShows(): List<TvShowsEntity> {
        return database.tvShowQueries.selectAll()
            .executeAsList()
            .toTvShowsEntityList()
    }

    override fun updateTvShowDetails(entity: TvShowsEntity) {
        database.tvShowQueries.updateTvShow(
            id = entity.id.toLong(),
            show_seasons = entity.seasonsList
        )
    }

    override fun deleteTvShows() {
        database.tvShowQueries.deleteAll()
    }
}