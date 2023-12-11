package com.thomaskioko.tvmaniac.shows.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.core.db.TvshowDetails
import com.thomaskioko.tvmaniac.core.db.Tvshows
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class DefaultTvShowsDao(
    database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : TvShowsDao {

    private val tvShowsQueries = database.tvshowsQueries

    override fun upsert(show: Tvshows) {
        tvShowsQueries.transaction {
            tvShowsQueries.upsert(
                id = show.id,
                name = show.name,
                overview = show.overview,
                language = show.language,
                first_air_date = show.first_air_date,
                vote_average = show.vote_average,
                vote_count = show.vote_count,
                popularity = show.popularity,
                genre_ids = show.genre_ids,
                status = show.status,
                episode_numbers = show.episode_numbers,
                last_air_date = show.last_air_date,
                season_numbers = show.season_numbers,
                poster_path = show.poster_path,
                backdrop_path = show.backdrop_path,
            )
        }
    }

    override fun upsert(list: List<Tvshows>) {
        list.forEach { upsert(it) }
    }

    override fun observeTvShows(id: Long): Flow<TvshowDetails> =
        tvShowsQueries.tvshowDetails(Id(id))
            .asFlow()
            .mapToOne(dispatchers.io)

    override fun getTvShow(id: Long): TvshowDetails =
        tvShowsQueries.tvshowDetails(Id(id))
            .executeAsOne()

    override fun deleteTvShow(id: Long) {
        tvShowsQueries.delete(Id(id))
    }

    override fun deleteTvShows() {
        tvShowsQueries.transaction {
            tvShowsQueries.deleteAll()
        }
    }
}
