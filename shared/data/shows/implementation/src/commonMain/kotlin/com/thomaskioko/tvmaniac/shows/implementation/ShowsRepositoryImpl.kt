package com.thomaskioko.tvmaniac.shows.implementation

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.util.DateFormatter
import com.thomaskioko.tvmaniac.util.ExceptionHandler
import com.thomaskioko.tvmaniac.category.api.cache.CategoryCache
import com.thomaskioko.tvmaniac.category.api.model.Category
import com.thomaskioko.tvmaniac.category.api.model.Category.ANTICIPATED
import com.thomaskioko.tvmaniac.category.api.model.Category.FEATURED
import com.thomaskioko.tvmaniac.category.api.model.Category.POPULAR
import com.thomaskioko.tvmaniac.category.api.model.Category.TRENDING
import com.thomaskioko.tvmaniac.core.db.Followed_shows
import com.thomaskioko.tvmaniac.core.db.SelectByShowId
import com.thomaskioko.tvmaniac.core.db.SelectFollowedShows
import com.thomaskioko.tvmaniac.core.db.SelectShowsByCategory
import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.networkutil.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.DefaultError
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import com.thomaskioko.tvmaniac.core.networkutil.networkBoundResult
import com.thomaskioko.tvmaniac.shows.api.ShowsRepository
import com.thomaskioko.tvmaniac.shows.api.cache.FollowedCache
import com.thomaskioko.tvmaniac.shows.api.cache.ShowsCache
import com.thomaskioko.tvmaniac.shows.implementation.mapper.ShowsResponseMapper
import com.thomaskioko.tvmaniac.trakt.api.TraktService
import com.thomaskioko.tvmaniac.trakt.api.model.ErrorResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

@Inject
class ShowsRepositoryImpl constructor(
    private val showsCache: ShowsCache,
    private val followedCache: FollowedCache,
    private val categoryCache: CategoryCache,
    private val traktService: TraktService,
    private val dateFormatter: DateFormatter,
    private val mapper: ShowsResponseMapper,
    private val exceptionHandler: ExceptionHandler,
    private val dispatchers: AppCoroutineDispatchers,
) : ShowsRepository {

    override fun observeShow(traktId: Long): Flow<Either<Failure, SelectByShowId>> =
        networkBoundResult(
            query = { showsCache.observeTvShow(traktId) },
            shouldFetch = { it == null },
            fetch = { traktService.getSeasonDetails(traktId) },
            saveFetchResult = { mapAndCache(it) },
            exceptionHandler = exceptionHandler,
            coroutineDispatcher = dispatchers.io
        )

    override fun observeCachedShows(categoryId: Long): Flow<Either<Failure, List<SelectShowsByCategory>>> =
        showsCache.observeCachedShows(Category[categoryId].id)
            .map { Either.Right(it) }
            .catch { Either.Left(DefaultError(exceptionHandler.resolveError(it))) }

    override fun fetchTrendingShows(): Flow<Either<Failure, List<SelectShowsByCategory>>> =
        networkBoundResult(
            query = { showsCache.observeCachedShows(TRENDING.id) },
            shouldFetch = { it.isNullOrEmpty() },
            fetch = { mapper.showsResponseToCacheList(traktService.getTrendingShows()) },
            saveFetchResult = { cacheResult(it, TRENDING.id) },
            exceptionHandler = exceptionHandler,
            coroutineDispatcher = dispatchers.io
        )

    override fun observeTrendingCachedShows(): Flow<Either<Failure, List<SelectShowsByCategory>>> =
        showsCache.observeCachedShows(TRENDING.id)
            .map { Either.Right(it) }
            .catch { Either.Left(DefaultError(exceptionHandler.resolveError(it))) }

    override fun fetchPopularShows(): Flow<Either<Failure, List<SelectShowsByCategory>>> =
        networkBoundResult(
            query = { showsCache.observeCachedShows(POPULAR.id) },
            shouldFetch = { it.isNullOrEmpty() },
            fetch = { mapper.showResponseToCacheList(traktService.getPopularShows()) },
            saveFetchResult = { cacheResult(it, POPULAR.id) },
            exceptionHandler = exceptionHandler,
            coroutineDispatcher = dispatchers.io
        )

    override fun observePopularCachedShows(): Flow<Either<Failure, List<SelectShowsByCategory>>> =
        showsCache.observeCachedShows(POPULAR.id)
            .map { Either.Right(it) }
            .catch { Either.Left(DefaultError(exceptionHandler.resolveError(it))) }

    override fun fetchAnticipatedShows(): Flow<Either<Failure, List<SelectShowsByCategory>>> =
        networkBoundResult(
            query = { showsCache.observeCachedShows(ANTICIPATED.id) },
            shouldFetch = { it.isNullOrEmpty() },
            fetch = { mapper.showsResponseToCacheList(traktService.getAnticipatedShows()) },
            saveFetchResult = { cacheResult(it, ANTICIPATED.id) },
            exceptionHandler = exceptionHandler,
            coroutineDispatcher = dispatchers.io
        )

    override fun observeAnticipatedCachedShows(): Flow<Either<Failure, List<SelectShowsByCategory>>> =
        showsCache.observeCachedShows(ANTICIPATED.id)
            .map { Either.Right(it) }
            .catch { Either.Left(DefaultError(exceptionHandler.resolveError(it))) }

    override fun fetchFeaturedShows(): Flow<Either<Failure, List<SelectShowsByCategory>>> =
        networkBoundResult(
            query = { showsCache.observeCachedShows(FEATURED.id) },
            shouldFetch = { it.isNullOrEmpty() },
            fetch = { mapper.showsResponseToCacheList(traktService.getRecommendedShows(period = "daily")) },
            saveFetchResult = { cacheResult(it, FEATURED.id) },
            exceptionHandler = exceptionHandler,
            coroutineDispatcher = dispatchers.io
        )

    override fun observeFeaturedCachedShows(): Flow<Either<Failure, List<SelectShowsByCategory>>> =
        showsCache.observeCachedShows(FEATURED.id)
            .map { Either.Right(it) }
            .catch { Either.Left(DefaultError(exceptionHandler.resolveError(it))) }

    override suspend fun fetchShows() {

        val categories = listOf(TRENDING, POPULAR, ANTICIPATED, FEATURED)

        categories.forEach {
            val mappedResult = fetchShowsAndMapResult(it.id)

            showsCache.insert(mappedResult)
            categoryCache.insert(mapper.toCategoryCache(mappedResult, it.id))
        }

    }

    override suspend fun updateFollowedShow(traktId: Long, addToWatchList: Boolean) {
        //TODO:: Check if user is signed into trakt and sync followed shows.
        when {
            addToWatchList -> followedCache.insert(
                Followed_shows(
                    id = traktId,
                    synced = false,
                    created_at = dateFormatter.getTimestampMilliseconds()
                )
            )

            else -> followedCache.removeShow(traktId)
        }
    }

    override fun observeFollowedShows(): Flow<Either<Failure, List<SelectFollowedShows>>> =
        followedCache.observeFollowedShows()
            .distinctUntilChanged()
            .map { Either.Right(it) }
            .catch { Either.Left(DefaultError(exceptionHandler.resolveError(it))) }

    override fun getFollowedShows(): List<SelectFollowedShows> = followedCache.getFollowedShows()

    private suspend fun fetchShowsAndMapResult(categoryId: Long): List<Show> =
        when (categoryId) {
            POPULAR.id -> mapper.showResponseToCacheList(traktService.getPopularShows())
            TRENDING.id -> mapper.showsResponseToCacheList(traktService.getTrendingShows())
            ANTICIPATED.id -> mapper.showsResponseToCacheList(traktService.getAnticipatedShows())
            FEATURED.id -> mapper.showsResponseToCacheList(traktService.getRecommendedShows(period = "daily"))

            else -> throw Throwable("Unsupported type sunny")
        }


    private fun cacheResult(result: List<Show>, categoryId: Long) {
        showsCache.insert(result)

        categoryCache.insert(mapper.toCategoryCache(result, categoryId))
    }

    private fun mapAndCache(response: ApiResponse<TraktShowResponse, ErrorResponse>) {
        when (response) {
            is ApiResponse.Success -> {
                showsCache.insert(mapper.responseToCache(response.body))
            }

            is ApiResponse.Error.GenericError -> {
                Logger.withTag("observeShow").e("$this")
                throw Throwable("${response.errorMessage}")
            }

            is ApiResponse.Error.HttpError -> {
                Logger.withTag("observeShow").e("$this")
                throw Throwable("${response.code} - ${response.errorBody?.message}")
            }

            is ApiResponse.Error.SerializationError -> {
                Logger.withTag("observeShow").e("$this")
                throw Throwable("$response")
            }
        }

    }
}