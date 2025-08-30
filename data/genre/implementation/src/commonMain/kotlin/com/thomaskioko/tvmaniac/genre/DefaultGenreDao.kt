package com.thomaskioko.tvmaniac.genre

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.Genres
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.db.Tvshow
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultGenreDao(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : GenreDao {
    private val genresQueries = database.genresQueries

    override fun upsert(entity: Genres) {
        database.transaction {
            genresQueries.upsert(
                id = entity.id,
                name = entity.name,
                poster_url = entity.poster_url,
            )
        }
    }

    override fun getGenres(): List<Genres> = genresQueries.genres().executeAsList()

    override fun getGenre(id: Long): Genres {
        return genresQueries.genreById(Id(id)).executeAsOne()
    }

    override fun observeGenres(): Flow<List<ShowGenresEntity>> {
        return genresQueries.genres { id, name, posterUrl ->
            ShowGenresEntity(
                id = id.id,
                name = name,
                posterUrl = posterUrl,
            )
        }
            .asFlow()
            .mapToList(dispatchers.io)
    }

    override fun observeShowsByGenreId(id: String): Flow<List<Tvshow>> {
        return database.showGenresQueries.showsByGenreId(Id(id.toLong()))
            .asFlow()
            .mapToList(dispatchers.io)
    }
}
