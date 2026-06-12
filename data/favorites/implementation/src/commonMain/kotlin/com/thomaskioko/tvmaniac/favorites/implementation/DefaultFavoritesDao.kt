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

    override fun upsert(showId: Long, rank: Long, listedAt: String) {
        val internalShowId = showIdResolver.showIdForTmdbId(showId) ?: return
        database.favoritesQueries.upsert(
            show_id = internalShowId,
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
        showId = show_id.id,
        tmdbId = tmdb_id.id,
        title = title,
        posterPath = poster_path,
        year = year,
    )
