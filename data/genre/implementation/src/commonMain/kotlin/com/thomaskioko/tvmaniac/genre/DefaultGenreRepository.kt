package com.thomaskioko.tvmaniac.genre

import com.thomaskioko.tvmaniac.db.Tvshow
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.get

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultGenreRepository(
    private val store: GenreStore,
    private val genrePosterStore: GenrePosterStore,
    private val showsByGenreIdStore: ShowsByGenreIdStore,
    private val genreDao: GenreDao,
) : GenreRepository {

    override suspend fun fetchGenresWithShows(forceRefresh: Boolean) {
        val isEmpty = genreDao.getGenres().isEmpty()
        when {
            forceRefresh || isEmpty -> store.fresh(Unit)
            else -> store.get(Unit)
        }
    }

    override suspend fun fetchShowByGenreId(id: String, forceRefresh: Boolean) {
        val isEmpty = genreDao.observeShowsByGenreId(id).first().isEmpty()
        when {
            forceRefresh || isEmpty -> showsByGenreIdStore.fresh(id)
            else -> showsByGenreIdStore.get(id)
        }
    }

    override fun observeGenresWithShows(): Flow<List<ShowGenresEntity>> = genreDao.observeGenres()

    override suspend fun observeShowByGenreId(id: String): Flow<List<Tvshow>> = genreDao.observeShowsByGenreId(id)

    override suspend fun observeGenrePosters() {
        genreDao.observeGenres()
            .collect { genres ->
                genres
                    .filter { it.posterUrl.isNullOrBlank() }
                    .forEach { genre ->
                        genrePosterStore.fresh(genre.id)
                    }
            }
    }
}
