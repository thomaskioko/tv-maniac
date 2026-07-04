package com.thomaskioko.tvmaniac.favorites.implementation

import com.thomaskioko.tvmaniac.accountmanager.testing.FakeProviderFeatures
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.favorites.api.FavoriteShow
import com.thomaskioko.tvmaniac.favorites.api.FavoritesDao
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.shows.api.ShowToPersist
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowDetailsResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowResult
import com.thomaskioko.tvmaniac.tmdb.api.model.WatchProvidersResult
import com.thomaskioko.tvmaniac.trakt.api.TraktUserRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktFavoriteShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktHiddenItemResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktPersonalListsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserStatsResponse
import com.thomaskioko.tvmaniac.util.testing.FakeFormatterUtil
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultFavoritesRepositoryGuardTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val requestManager = FakeRequestManagerRepository(initialRequestValid = false)
    private val trackingTraktSource = TrackingTraktSource()

    private lateinit var repository: DefaultFavoritesRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    private fun buildRepository(activeProviderFeatures: () -> FakeProviderFeatures): DefaultFavoritesRepository {
        val favoritesStore = FavoritesStore(
            traktUserDataSource = trackingTraktSource,
            tmdbDataSource = NoOpTmdbDataSource,
            favoritesDao = NoOpFavDao,
            tvShowsDao = NoOpTvShowsDao,
            requestManagerRepository = requestManager,
            transactionRunner = ImmediateRunner,
            formatterUtil = FakeFormatterUtil(),
            dispatchers = dispatchers,
        )
        return DefaultFavoritesRepository(
            dao = NoOpFavDao,
            favoritesStore = favoritesStore,
            activeProviderFeatures = activeProviderFeatures,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should skip network fetch given no provider is active`() = runTest(testDispatcher) {
        repository = buildRepository { FakeProviderFeatures(supportsFavorites = false) }

        repository.syncFavorites(forceRefresh = false)

        trackingTraktSource.fetchFavoritesCount shouldBe 0
    }

    @Test
    fun `should skip network fetch given simkl provider is active`() = runTest(testDispatcher) {
        repository = buildRepository { FakeProviderFeatures(supportsFavorites = false) }

        repository.syncFavorites(forceRefresh = false)

        trackingTraktSource.fetchFavoritesCount shouldBe 0
    }
}

private class TrackingTraktSource : TraktUserRemoteDataSource {
    var fetchFavoritesCount: Int = 0

    override suspend fun getUser(userId: String): ApiResponse<TraktUserResponse> = ApiResponse.Unauthenticated

    override suspend fun getUserStats(userId: String): ApiResponse<TraktUserStatsResponse> = ApiResponse.Unauthenticated

    override suspend fun getUserList(userId: String): List<TraktPersonalListsResponse> = emptyList()

    override suspend fun getFavoriteShows(userId: String): ApiResponse<List<TraktFavoriteShowResponse>> {
        fetchFavoritesCount++
        return ApiResponse.Success(emptyList())
    }

    override suspend fun getHiddenProgressWatched(type: String): ApiResponse<List<TraktHiddenItemResponse>> =
        ApiResponse.Unauthenticated
}

private object NoOpTmdbDataSource : TmdbShowDetailsNetworkDataSource {
    override suspend fun getShowDetails(id: Long): ApiResponse<TmdbShowDetailsResponse> = ApiResponse.Unauthenticated
    override suspend fun getSimilarShows(id: Long, page: Long): ApiResponse<TmdbShowResult> = ApiResponse.Unauthenticated
    override suspend fun getRecommendedShows(id: Long, page: Long): ApiResponse<TmdbShowResult> = ApiResponse.Unauthenticated
    override suspend fun getShowWatchProviders(id: Long): ApiResponse<WatchProvidersResult> = ApiResponse.Unauthenticated
}

private object NoOpFavDao : FavoritesDao {
    override fun observeFavoriteShows(): Flow<List<FavoriteShow>> = flowOf(emptyList())
    override fun upsert(showId: Long, rank: Long, listedAt: String) {}
    override fun deleteAll() {}
}

private object NoOpTvShowsDao : TvShowsDao {
    override fun upsert(show: ShowToPersist) {}
    override fun upsert(list: List<ShowToPersist>) {}
    override fun observeShowsByQuery(query: String): Flow<List<ShowEntity>> = flowOf(emptyList())
    override fun observeQueryCount(query: String): Flow<Long> = flowOf(0L)
    override suspend fun getQueryCount(query: String): Long = 0L
    override fun deleteTvShows() {}
    override fun upsertMerging(show: ShowToPersist) {}
    override fun getShowsByIds(showIds: List<Long>): List<ShowEntity> = emptyList()
    override fun getTmdbIdByShowId(showId: Long): Long? = null
    override fun getTmdbIdForLocalShowId(showId: Long): Long? = null
    override fun getTraktIdByTmdbId(tmdbId: Long): Long? = null
    override suspend fun existsByShowId(showId: Long): Boolean = false
}

private object ImmediateRunner : DatabaseTransactionRunner {
    override fun <T> invoke(block: () -> T): T = block()
}
