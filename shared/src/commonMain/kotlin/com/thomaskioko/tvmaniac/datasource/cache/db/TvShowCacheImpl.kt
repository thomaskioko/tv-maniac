package com.thomaskioko.tvmaniac.datasource.cache.db

import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase
import com.thomaskioko.tvmaniac.datasource.cache.mapper.ShowEntityMapper.toTvShowsEntity
import com.thomaskioko.tvmaniac.datasource.cache.mapper.ShowEntityMapper.toTvShowsEntityList
import com.thomaskioko.tvmaniac.datasource.cache.model.TvShowsEntity

class TvShowCacheImpl(
    private val database: TvManiacDatabase
) : TvShowCache {

    override fun insert(entity: TvShowsEntity) {
        database.tvShowQueries.insertOrReplace(
            show_id = entity.showId.toLong(),
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

    override fun insert(entityList: List<TvShowsEntity>) {
        entityList.forEach { insert(it) }
    }

    override fun getTvShow(showId: Int): TvShowsEntity? {
        return database.tvShowQueries.selectByShowId(
            show_id = showId.toLong()
        ).executeAsOneOrNull()?.toTvShowsEntity()
    }


    override fun getTvShows(): List<TvShowsEntity> {
        return database.tvShowQueries.selectAll()
            .executeAsList()
            .toTvShowsEntityList()
    }

    override fun deleteTvShows() {
        database.tvShowQueries.deleteAll()
    }
}