package com.thomaskioko.tvmaniac.shows.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Provider
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.trakt.api.TimePeriod
import com.thomaskioko.tvmaniac.trakt.api.TraktShowsRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.ShowIds
import com.thomaskioko.tvmaniac.trakt.api.model.TraktGenreResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSearchResult
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowPeopleResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktVideosResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedProgressResponse
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class TraktShowIdResolverTest : BaseDatabaseTest() {

    private val remoteDataSource = RecordingTraktShowsSource()
    private val accountManager = FakeAccountManager()
    private lateinit var resolver: TraktShowIdResolver

    @BeforeTest
    fun setUp() {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        resolver = TraktShowIdResolver(
            remoteDataSource = remoteDataSource,
            accountManager = accountManager,
            database = database,
            logger = FakeLogger(),
        )
        insertShow(BREAKING_BAD_TMDB_ID)
    }

    @AfterTest
    fun tearDown() {
        closeDb()
    }

    @Test
    fun `should persist the trakt id given trakt matches the show`() = runTest {
        remoteDataSource.setResult(BREAKING_BAD_TMDB_ID, TRAKT_ID)

        val resolved = resolver.resolveMissingTraktIds(listOf(BREAKING_BAD_TMDB_ID))

        resolved shouldBe 1
        traktIdFor(BREAKING_BAD_TMDB_ID) shouldBe TRAKT_ID.toString()
    }

    @Test
    fun `should leave the show alone given trakt has no match`() = runTest {
        remoteDataSource.setEmpty(BREAKING_BAD_TMDB_ID)

        val resolved = resolver.resolveMissingTraktIds(listOf(BREAKING_BAD_TMDB_ID))

        resolved shouldBe 0
        traktIdFor(BREAKING_BAD_TMDB_ID).shouldBeNull()
    }

    @Test
    fun `should keep going given one show fails to resolve`() = runTest {
        insertShow(FOR_ALL_MANKIND_TMDB_ID)
        remoteDataSource.setFailure(BREAKING_BAD_TMDB_ID)
        remoteDataSource.setResult(FOR_ALL_MANKIND_TMDB_ID, TRAKT_ID)

        val resolved = resolver.resolveMissingTraktIds(
            listOf(BREAKING_BAD_TMDB_ID, FOR_ALL_MANKIND_TMDB_ID),
        )

        resolved shouldBe 1
        traktIdFor(BREAKING_BAD_TMDB_ID).shouldBeNull()
        traktIdFor(FOR_ALL_MANKIND_TMDB_ID) shouldBe TRAKT_ID.toString()
    }

    @Test
    fun `should request nothing given no trakt account is connected`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.SIMKL)

        val resolved = resolver.resolveMissingTraktIds(listOf(BREAKING_BAD_TMDB_ID))

        resolved shouldBe 0
        remoteDataSource.lookupCount shouldBe 0
    }

    @Test
    fun `should request nothing given the show already has a trakt id`() = runTest {
        showIdForTraktId(traktId = TRAKT_ID, tmdbId = BREAKING_BAD_TMDB_ID)

        val resolved = resolver.resolveMissingTraktIds(listOf(BREAKING_BAD_TMDB_ID))

        resolved shouldBe 0
        remoteDataSource.lookupCount shouldBe 0
    }

    private fun traktIdFor(tmdbId: Long): String? {
        val showId = database.tvShowQueries.getShowIdByTmdbId(Id<TmdbId>(tmdbId)).executeAsOne()
        return database.tvshowExternalIdQueries
            .externalIdForShow(showId = showId, provider = Provider.TRAKT)
            .executeAsOneOrNull()
    }

    private fun insertShow(tmdbId: Long) {
        database.tvShowQueries.upsert(
            tmdb_id = Id<TmdbId>(tmdbId),
            name = "Restored Show",
            overview = "",
            language = null,
            year = null,
            ratings = 0.0,
            vote_count = 0,
            genres = null,
            status = null,
            episode_numbers = null,
            season_numbers = null,
            poster_path = null,
            backdrop_path = null,
        )
    }

    private companion object {
        private const val BREAKING_BAD_TMDB_ID = 1396L
        private const val FOR_ALL_MANKIND_TMDB_ID = 87917L
        private const val TRAKT_ID = 1388L
    }
}

private class RecordingTraktShowsSource : TraktShowsRemoteDataSource {

    private val lookups = mutableMapOf<Long, ApiResponse<List<TraktSearchResult>>>()
    var lookupCount: Int = 0
        private set

    fun setResult(tmdbId: Long, traktId: Long) {
        lookups[tmdbId] = ApiResponse.Success(
            listOf(
                TraktSearchResult(
                    type = "show",
                    show = TraktShowResponse(
                        title = "Restored Show",
                        ids = ShowIds(trakt = traktId, tmdb = tmdbId),
                    ),
                ),
            ),
        )
    }

    fun setEmpty(tmdbId: Long) {
        lookups[tmdbId] = ApiResponse.Success(emptyList())
    }

    fun setFailure(tmdbId: Long) {
        lookups[tmdbId] = ApiResponse.Error.HttpError(code = 500, errorBody = null, errorMessage = "boom")
    }

    override suspend fun getShowByTmdbId(tmdbId: Long): ApiResponse<List<TraktSearchResult>> {
        lookupCount++
        return lookups[tmdbId] ?: error("RecordingTraktShowsSource: no lookup set for $tmdbId")
    }

    override suspend fun getTrendingShows(page: Int, limit: Int, genres: String?): ApiResponse<List<TraktShowsResponse>> = error("not configured")
    override suspend fun getGenres(): ApiResponse<List<TraktGenreResponse>> = error("not configured")
    override suspend fun getPopularShows(page: Int, limit: Int, genres: String?): ApiResponse<List<TraktShowResponse>> = error("not configured")
    override suspend fun getFavoritedShows(page: Int, limit: Int, period: TimePeriod, genres: String?): ApiResponse<List<TraktShowsResponse>> = error("not configured")
    override suspend fun getMostWatchedShows(page: Int, limit: Int, period: TimePeriod, genres: String?): ApiResponse<List<TraktShowsResponse>> = error("not configured")
    override suspend fun getRelatedShows(showId: Long, page: Int, limit: Int): ApiResponse<List<TraktShowResponse>> = error("not configured")
    override suspend fun getShowDetails(showId: Long): ApiResponse<TraktShowResponse> = error("not configured")
    override suspend fun searchShows(query: String, page: Int, limit: Int): ApiResponse<List<TraktSearchResult>> = error("not configured")
    override suspend fun getShowPeople(showId: Long): ApiResponse<TraktShowPeopleResponse> = error("not configured")
    override suspend fun getShowVideos(showId: Long): ApiResponse<List<TraktVideosResponse>> = error("not configured")
    override suspend fun getWatchedProgress(showId: Long): ApiResponse<TraktWatchedProgressResponse> = error("not configured")
}
