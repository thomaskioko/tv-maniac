package com.thomaskioko.tvmaniac.genre

import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.db.Tvshow
import com.thomaskioko.tvmaniac.genre.model.GenreShowCategory
import com.thomaskioko.tvmaniac.genre.model.GenreShowsStoreKey
import com.thomaskioko.tvmaniac.genre.model.GenreWithShowsEntity
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.GENRE_SHOWS
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.get
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@OptIn(ExperimentalCoroutinesApi::class)
@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultGenreRepository(
    private val store: GenreStore,
    private val genrePosterStore: GenrePosterStore,
    private val showsByGenreIdStore: ShowsByGenreIdStore,
    private val genreDao: GenreDao,
    private val traktGenresStore: TraktGenresStore,
    private val genreShowsStore: GenreShowsStore,
    private val traktGenreDao: TraktGenreDao,
    private val datastoreRepository: DatastoreRepository,
    private val requestManagerRepository: RequestManagerRepository,
) : GenreRepository {

    override suspend fun saveGenreShowCategory(category: GenreShowCategory) {
        datastoreRepository.saveGenreShowCategory(category.name)
    }

    override suspend fun getGenreShowCategory(): GenreShowCategory {
        val name = datastoreRepository.getGenreShowCategory()
        return GenreShowCategory.entries.find { it.name == name } ?: GenreShowCategory.POPULAR
    }

    override fun observeGenreShowCategory(): Flow<GenreShowCategory> =
        datastoreRepository.observeGenreShowCategory().map { name ->
            GenreShowCategory.entries.find { it.name == name } ?: GenreShowCategory.POPULAR
        }

    override suspend fun fetchGenresWithShows(forceRefresh: Boolean) {
        val isEmpty = genreDao.getGenres().isEmpty()
        when {
            forceRefresh || isEmpty -> store.fresh(Unit)
            else -> store.get(Unit)
        }
    }

    override suspend fun fetchShowByGenreId(id: String, forceRefresh: Boolean) {
        when {
            forceRefresh -> showsByGenreIdStore.fresh(id)
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

    override fun getGenreSlugs(): List<String> = traktGenreDao.getGenreSlugs()

    override suspend fun fetchTraktGenres(forceRefresh: Boolean) {
        when {
            forceRefresh -> traktGenresStore.fresh(Unit)
            else -> traktGenresStore.get(Unit)
        }
    }

    override suspend fun fetchGenreShows(slug: String, category: GenreShowCategory, forceRefresh: Boolean) {
        val key = GenreShowsStoreKey(genreSlug = slug, category = category)
        val entityId = "${slug}_${category.name}".hashCode().toLong()
        val isExpired = requestManagerRepository.isRequestExpired(
            entityId = entityId,
            requestType = GENRE_SHOWS.name,
            threshold = GENRE_SHOWS.duration,
        )
        when {
            forceRefresh || isExpired -> genreShowsStore.fresh(key)
            else -> genreShowsStore.get(key)
        }
    }

    override fun observeGenresWithShowRows(): Flow<List<GenreWithShowsEntity>> =
        observeGenreShowCategory().flatMapLatest { category ->
            traktGenreDao.observeGenresWithShowsByCategory(category.name)
        }
}
