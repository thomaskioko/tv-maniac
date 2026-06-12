package com.thomaskioko.tvmaniac.data.showdetails.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsDao
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.db.TvshowDetails
import com.thomaskioko.tvmaniac.db.TvshowDetailsByTmdbId
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultShowDetailsDao(
    database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : ShowDetailsDao {
    private val tvShowQueries = database.tvShowQueries

    override fun observeTvShowByShowId(showId: Long): Flow<TvshowDetails?> =
        tvShowQueries.tvshowDetailsByTmdbId(Id<TmdbId>(showId))
            .asFlow()
            .mapToOneOrNull(dispatchers.io)
            .map { it?.toTvshowDetails() }

    override fun getTvShow(showId: Long): TvshowDetails =
        tvShowQueries.tvshowDetailsByTmdbId(Id<TmdbId>(showId)).executeAsOne().toTvshowDetails()

    override fun getTvShowOrNull(showId: Long): TvshowDetails? =
        tvShowQueries.tvshowDetailsByTmdbId(Id<TmdbId>(showId)).executeAsOneOrNull()?.toTvshowDetails()

    override fun deleteTvShow(showId: Long) {
        tvShowQueries.delete(showId)
    }
}

private fun TvshowDetailsByTmdbId.toTvshowDetails(): TvshowDetails = TvshowDetails(
    trakt_id = trakt_id ?: 0L,
    tmdb_id = tmdb_id,
    name = name,
    overview = overview,
    language = language,
    year = year,
    ratings = ratings,
    status = status,
    vote_count = vote_count,
    poster_path = poster_path,
    backdrop_path = backdrop_path,
    genres = genres,
    season_numbers = season_numbers,
    in_library = in_library,
)
