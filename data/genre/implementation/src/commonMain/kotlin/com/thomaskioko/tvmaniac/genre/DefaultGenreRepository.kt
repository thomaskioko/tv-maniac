package com.thomaskioko.tvmaniac.genre

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.paging.FetchResult
import com.thomaskioko.tvmaniac.core.paging.PaginatedRemoteMediator
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.db.Tvshow
import com.thomaskioko.tvmaniac.genre.model.GenreShowCategory
import com.thomaskioko.tvmaniac.genre.model.GenreShowsStoreKey
import com.thomaskioko.tvmaniac.genre.model.GenreWithShowsEntity
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.GENRE_SHOWS
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.get

@OptIn(ExperimentalCoroutinesApi::class)
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
    private val logger: Logger,
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
        val isExpired = requestManagerRepository.isRequestExpired(
            entityId = genreShowsRequestId(slug, category, key.page),
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

    override fun getPagedGenreShows(
        slug: String,
        category: GenreShowCategory,
        forceRefresh: Boolean,
    ): Flow<PagingData<ShowEntity>> = Pager(
        config = PagingConfig(pageSize = GENRE_PAGE_SIZE, initialLoadSize = GENRE_PAGE_SIZE),
        remoteMediator = PaginatedRemoteMediator(logger = logger, source = "GenreShows") { page ->
            fetchGenrePage(slug, category, page, forceRefresh)
        },
        pagingSourceFactory = { traktGenreDao.getPagedShowsByGenreSlugAndCategory(slug, category.name) },
    ).flow

    private suspend fun fetchGenrePage(slug: String, category: GenreShowCategory, page: Long, forceRefresh: Boolean): FetchResult {
        return if (shouldFetchGenrePage(slug, category, page, forceRefresh)) {
            try {
                val result = genreShowsStore.fresh(GenreShowsStoreKey(genreSlug = slug, category = category, page = page))
                FetchResult.Success(endOfPaginationReached = result.isEmpty())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                logger.error("Error while fetching from GenreShows RemoteMediator", e)
                FetchResult.Error(e)
            }
        } else {
            FetchResult.NoFetch
        }
    }

    internal fun shouldFetchGenrePage(slug: String, category: GenreShowCategory, page: Long, forceRefresh: Boolean): Boolean {
        if (forceRefresh) return true
        val pageExists = traktGenreDao.pageExists(slug, category.name, page)
        return !pageExists || isGenreRequestExpired(slug, category, page)
    }

    private fun isGenreRequestExpired(slug: String, category: GenreShowCategory, page: Long): Boolean =
        requestManagerRepository.isRequestExpired(
            entityId = genreShowsRequestId(slug, category, page),
            requestType = GENRE_SHOWS.name,
            threshold = GENRE_SHOWS.duration,
        )
}
