package com.thomaskioko.tvmaniac.favorites.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.FavoriteShows
import com.thomaskioko.tvmaniac.db.ShowIdResolver
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.favorites.api.FavoriteShow
import com.thomaskioko.tvmaniac.favorites.api.FavoritesDao
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultFavoritesDao(
    private val database: TvManiacDatabase,
    private val showIdResolver: ShowIdResolver,
    private val dispatchers: AppCoroutineDispatchers,
) : FavoritesDao {

    override fun observeFavoriteShows(): Flow<List<FavoriteShow>> =
        database.favoritesQueries.favoriteShows()
            .asFlow()
            .mapToList(dispatchers.databaseRead)
            .map { rows -> rows.map { it.toFavoriteShow() } }
            .catch { emit(emptyList()) }

    override fun upsert(traktId: Long, rank: Long, listedAt: String) {
        val showId = showIdResolver.showIdForTraktId(traktId) ?: return
        database.favoritesQueries.upsert(
            show_id = showId,
            rank = rank,
            listed_at = listedAt,
        )
    }

    override fun deleteAll() {
        database.favoritesQueries.deleteAll()
    }
}

private fun FavoriteShows.toFavoriteShow(): FavoriteShow =
    FavoriteShow(
        traktId = show_trakt_id.id,
        tmdbId = show_tmdb_id.id,
        title = title,
        posterPath = poster_path,
        year = year,
    )
