package com.thomaskioko.tvmaniac.traktlists.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.DbTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.shows.testing.FakeTvShowsDao
import com.thomaskioko.tvmaniac.trakt.api.TraktListRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.IdsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.ListIds
import com.thomaskioko.tvmaniac.trakt.api.model.ShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddRemoveShowFromListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddShowToListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddedShowsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktCreateListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktDeletedShowsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktExistingShowsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktFollowedShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktListItemResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktNotFoundShowsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktPersonalListsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserResponse
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultTraktListRepositoryTest : BaseDatabaseTest() {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var listDao: DefaultTraktListDao
    private lateinit var showDao: DefaultTraktListShowDao
    private lateinit var transactionRunner: DatabaseTransactionRunner
    private lateinit var remoteDataSource: FakeRemoteDataSource
    private lateinit var requestManager: FakeRequestManagerRepository
    private lateinit var tvShowsDao: FakeTvShowsDao
    private lateinit var repository: DefaultTraktListRepository

    @BeforeTest
    fun setUp() {
        listDao = DefaultTraktListDao(database, dispatchers)
        showDao = DefaultTraktListShowDao(database, dispatchers)
        transactionRunner = DbTransactionRunner(database)
        remoteDataSource = FakeRemoteDataSource()
        requestManager = FakeRequestManagerRepository().apply { requestValid = false }
        tvShowsDao = FakeTvShowsDao()

        val listsStore = TraktListsStore(
            traktListDataSource = remoteDataSource,
            traktListDao = listDao,
            requestManagerRepository = requestManager,
            transactionRunner = transactionRunner,
            dispatchers = dispatchers,
        )
        val itemsStore = TraktListItemsStore(
            traktListRemoteDataSource = remoteDataSource,
            traktListShowDao = showDao,
            requestManagerRepository = requestManager,
            transactionRunner = transactionRunner,
            dispatchers = dispatchers,
        )
        val createStore = CreateTraktListStore(
            traktListRemoteDataSource = remoteDataSource,
            traktListDao = listDao,
            dateTimeProvider = FakeDateTimeProvider(),
        )
        repository = DefaultTraktListRepository(
            traktListsStore = listsStore,
            traktListItemsStore = itemsStore,
            createTraktListStore = createStore,
            traktListDao = listDao,
            traktListShowDao = showDao,
            traktListRemoteDataSource = remoteDataSource,
            tvShowsDao = tvShowsDao,
            dispatchers = dispatchers,
        )
    }

    @AfterTest
    fun tearDown() {
        closeDb()
    }

    @Test
    fun `should populate junction with synced items given fetchUserLists is called`() = runTest {
        remoteDataSource.lists = listOf(traktListResponse(id = 1L, slug = "watchlist", itemCount = 2))
        remoteDataSource.itemsByListId = mapOf(
            1L to listOf(
                traktListItemResponse(traktId = 10L, tmdbId = 100L),
                traktListItemResponse(traktId = 20L, tmdbId = 200L),
            ),
        )

        repository.fetchUserLists(slug = "sean", forceRefresh = true)

        val counts = showDao.observeActiveCountByListId().first()
        counts[1L] shouldBe 2L
    }

    @Test
    fun `should sync remaining lists given one list items request returns not found`() = runTest {
        remoteDataSource.lists = listOf(
            traktListResponse(id = 1L, slug = "watchlist", itemCount = 2),
            traktListResponse(id = 2L, slug = "favorites", itemCount = 1),
        )
        remoteDataSource.itemsByListId = mapOf(
            1L to listOf(
                traktListItemResponse(traktId = 10L, tmdbId = 100L),
                traktListItemResponse(traktId = 20L, tmdbId = 200L),
            ),
        )
        remoteDataSource.itemsErrorByListId = mapOf(
            2L to ApiResponse.Error.HttpError(
                code = 404,
                errorBody = null,
                errorMessage = "Status: 404  Failure: Invalid Request",
            ),
        )

        repository.fetchUserLists(slug = "sean", forceRefresh = true)

        val counts = showDao.observeActiveCountByListId().first()
        counts[1L] shouldBe 2L
    }

    @Test
    fun `should preserve pending UPLOAD rows given items sync replaces synced rows`() = runTest {
        seedShow(tmdbId = 990L, traktId = 99L)
        remoteDataSource.lists = listOf(traktListResponse(id = 1L, slug = "watchlist", itemCount = 1))
        remoteDataSource.itemsByListId = mapOf(
            1L to listOf(traktListItemResponse(traktId = 10L, tmdbId = 100L)),
        )
        showDao.upsert(
            listId = 1L,
            traktId = 99L,
            listedAt = "",
            pendingAction = PendingAction.UPLOAD.value,
        )

        repository.fetchUserLists(slug = "sean", forceRefresh = true)

        val entries = showDao.observeByShowId(990L).first()
        entries.size shouldBe 1
        entries[0].pendingAction shouldBe PendingAction.UPLOAD.value
        val counts = showDao.observeActiveCountByListId().first()
        counts[1L] shouldBe 2L
    }

    @Test
    fun `should preserve pending DELETE rows given items sync replaces synced rows`() = runTest {
        seedShow(tmdbId = 200L, traktId = 20L)
        remoteDataSource.lists = listOf(traktListResponse(id = 1L, slug = "watchlist", itemCount = 2))
        remoteDataSource.itemsByListId = mapOf(
            1L to listOf(
                traktListItemResponse(traktId = 10L, tmdbId = 100L),
                traktListItemResponse(traktId = 20L, tmdbId = 200L),
            ),
        )
        showDao.upsert(
            listId = 1L,
            traktId = 20L,
            listedAt = "",
            pendingAction = PendingAction.DELETE.value,
        )

        repository.fetchUserLists(slug = "sean", forceRefresh = true)

        val entries = showDao.observeByShowId(200L).first()
        entries.size shouldBe 1
        entries[0].pendingAction shouldBe PendingAction.DELETE.value
        val counts = showDao.observeActiveCountByListId().first()
        counts[1L] shouldBe 1L
    }

    @Test
    fun `should reflect synced shows in observeListsForShow given user has multiple lists`() = runTest {
        seedShow(tmdbId = 100L, traktId = 10L)
        remoteDataSource.lists = listOf(
            traktListResponse(id = 1L, slug = "watchlist", itemCount = 3),
            traktListResponse(id = 2L, slug = "favorites", itemCount = 1),
        )
        remoteDataSource.itemsByListId = mapOf(
            1L to listOf(
                traktListItemResponse(traktId = 10L, tmdbId = 100L),
                traktListItemResponse(traktId = 20L, tmdbId = 200L),
                traktListItemResponse(traktId = 30L, tmdbId = 300L),
            ),
            2L to listOf(traktListItemResponse(traktId = 10L, tmdbId = 100L)),
        )

        repository.fetchUserLists(slug = "sean", forceRefresh = true)

        val lists = repository.observeListsForShow(showId = 100L).first()
        lists.map { it.id to it.itemCount } shouldContainExactlyInAnyOrder listOf(
            1L to 3L,
            2L to 1L,
        )
        lists.first { it.id == 1L }.isShowInList shouldBe true
        lists.first { it.id == 2L }.isShowInList shouldBe true
    }

    @Test
    fun `should skip items fetch given validator reports items are fresh`() = runTest {
        remoteDataSource.lists = listOf(traktListResponse(id = 1L, slug = "watchlist", itemCount = 0))
        remoteDataSource.itemsByListId = mapOf(1L to emptyList())

        repository.fetchUserLists(slug = "sean", forceRefresh = true)
        val callsAfterFirstSync = remoteDataSource.itemsCalls.size

        requestManager.requestValid = true
        repository.fetchUserLists(slug = "sean", forceRefresh = false)

        remoteDataSource.itemsCalls.size shouldBe callsAfterFirstSync
    }

    @Test
    fun `should sync items for each list given fetchUserLists is called`() = runTest {
        remoteDataSource.lists = listOf(
            traktListResponse(id = 1L, slug = "watchlist", itemCount = 0),
            traktListResponse(id = 2L, slug = "favorites", itemCount = 0),
        )
        remoteDataSource.itemsByListId = mapOf(
            1L to emptyList(),
            2L to emptyList(),
        )

        repository.fetchUserLists(slug = "sean", forceRefresh = true)

        remoteDataSource.itemsCalls shouldContainExactlyInAnyOrder listOf(
            "sean" to 1L,
            "sean" to 2L,
        )
    }

    @Test
    fun `should store trakt id and call remote with trakt id given show is added to list`() = runTest {
        seedShow(tmdbId = 100L, traktId = 10L)
        seedList(listId = 1L)

        repository.toggleShowInList(slug = "sean", listId = 1L, showId = 100L, isCurrentlyInList = false)

        remoteDataSource.addToListCalls shouldBe listOf(Triple("sean", 1L, 10L))
        val lists = repository.observeListsForShow(showId = 100L).first()
        lists.first { it.id == 1L }.isShowInList shouldBe true
        showDao.countPendingActions() shouldBe 0L
    }

    @Test
    fun `should keep added show given items sync runs after upload`() = runTest {
        seedShow(tmdbId = 100L, traktId = 10L)
        seedList(listId = 1L)

        repository.toggleShowInList(slug = "sean", listId = 1L, showId = 100L, isCurrentlyInList = false)

        remoteDataSource.itemsByListId = mapOf(
            1L to listOf(traktListItemResponse(traktId = 10L, tmdbId = 100L)),
        )
        repository.fetchUserLists(slug = "sean", forceRefresh = true)

        val lists = repository.observeListsForShow(showId = 100L).first()
        lists.first { it.id == 1L }.isShowInList shouldBe true
    }

    @Test
    fun `should revert junction entry given remote add fails`() = runTest {
        seedShow(tmdbId = 100L, traktId = 10L)
        seedList(listId = 1L)
        remoteDataSource.addToListResponse = ApiResponse.Error.HttpError(
            code = 500,
            errorBody = null,
            errorMessage = "server error",
        )

        repository.toggleShowInList(slug = "sean", listId = 1L, showId = 100L, isCurrentlyInList = false)

        val lists = repository.observeListsForShow(showId = 100L).first()
        lists.first { it.id == 1L }.isShowInList shouldBe false
        showDao.countPendingActions() shouldBe 0L
    }

    @Test
    fun `should delete junction entry and call remote with trakt id given show is removed from list`() = runTest {
        seedShow(tmdbId = 100L, traktId = 10L)
        seedList(listId = 1L)
        showDao.upsertSynced(listId = 1L, traktId = 10L, listedAt = "")

        repository.toggleShowInList(slug = "sean", listId = 1L, showId = 100L, isCurrentlyInList = true)

        remoteDataSource.removeFromListCalls shouldBe listOf(Triple("sean", 1L, 10L))
        val lists = repository.observeListsForShow(showId = 100L).first()
        lists.first { it.id == 1L }.isShowInList shouldBe false
    }

    @Test
    fun `should restore junction entry given remote remove fails`() = runTest {
        seedShow(tmdbId = 100L, traktId = 10L)
        seedList(listId = 1L)
        showDao.upsertSynced(listId = 1L, traktId = 10L, listedAt = "")
        remoteDataSource.removeFromListResponse = ApiResponse.Error.HttpError(
            code = 500,
            errorBody = null,
            errorMessage = "server error",
        )

        repository.toggleShowInList(slug = "sean", listId = 1L, showId = 100L, isCurrentlyInList = true)

        val lists = repository.observeListsForShow(showId = 100L).first()
        lists.first { it.id == 1L }.isShowInList shouldBe true
        showDao.countPendingActions() shouldBe 0L
    }

    @Test
    fun `should fail given show has no trakt id mapping`() = runTest {
        seedList(listId = 1L)

        shouldThrow<IllegalArgumentException> {
            repository.toggleShowInList(slug = "sean", listId = 1L, showId = 100L, isCurrentlyInList = false)
        }
    }

    private fun seedShow(tmdbId: Long, traktId: Long) {
        database.tvShowQueries.upsert(
            tmdb_id = Id<TmdbId>(tmdbId),
            name = "Show $tmdbId",
            overview = "Overview",
            language = "en",
            year = "2024-01-01",
            ratings = 8.0,
            vote_count = 100,
            genres = listOf("Drama"),
            status = "Returning Series",
            episode_numbers = null,
            season_numbers = null,
            poster_path = null,
            backdrop_path = null,
        )
        showIdForTraktId(traktId = traktId, tmdbId = tmdbId)
        tvShowsDao.setTraktIdForTmdbId(tmdbId = tmdbId, traktId = traktId)
    }

    private suspend fun seedList(listId: Long) {
        remoteDataSource.lists = listOf(traktListResponse(id = listId, slug = "watchlist", itemCount = 0))
        remoteDataSource.itemsByListId = mapOf(listId to emptyList())
        repository.fetchUserLists(slug = "sean", forceRefresh = true)
    }

    private fun traktListResponse(id: Long, slug: String, itemCount: Int) =
        TraktPersonalListsResponse(
            allowComments = true,
            commentCount = 0,
            createdAt = "2024-01-01T00:00:00.000Z",
            description = "",
            display_numbers = false,
            ids = ListIds(trakt = id.toInt(), slug = slug),
            item_count = itemCount,
            likes = 0,
            name = slug,
            privacy = "private",
            sort_by = "added",
            sort_how = "asc",
            updated_at = "2024-01-01T00:00:00.000Z",
        )

    private fun traktListItemResponse(traktId: Long, tmdbId: Long) = TraktListItemResponse(
        listedAt = "2024-01-01T00:00:00.000Z",
        type = "show",
        show = ShowResponse(
            title = "Show $traktId",
            year = 2024,
            ids = IdsResponse(slug = "show-$traktId", trakt = traktId, tmdb = tmdbId),
        ),
    )
}

private class FakeRemoteDataSource : TraktListRemoteDataSource {
    var lists: List<TraktPersonalListsResponse> = emptyList()
    var itemsByListId: Map<Long, List<TraktListItemResponse>> = emptyMap()
    var itemsErrorByListId: Map<Long, ApiResponse<List<TraktListItemResponse>>> = emptyMap()
    val itemsCalls: MutableList<Pair<String, Long>> = mutableListOf()
    val addToListCalls: MutableList<Triple<String, Long, Long>> = mutableListOf()
    val removeFromListCalls: MutableList<Triple<String, Long, Long>> = mutableListOf()
    var addToListResponse: ApiResponse<TraktAddShowToListResponse> = ApiResponse.Success(
        TraktAddShowToListResponse(
            added = TraktAddedShowsResponse(shows = 1),
            existing = TraktExistingShowsResponse(shows = 0),
            notFound = TraktNotFoundShowsResponse(shows = emptyList()),
            list = TraktListResponse(itemCount = 1, updateAdd = "2024-01-01T00:00:00.000Z"),
        ),
    )
    var removeFromListResponse: ApiResponse<TraktAddRemoveShowFromListResponse> = ApiResponse.Success(
        TraktAddRemoveShowFromListResponse(
            deleted = TraktDeletedShowsResponse(shows = 1),
            notFound = TraktNotFoundShowsResponse(shows = emptyList()),
            list = TraktListResponse(itemCount = 0, updateAdd = "2024-01-01T00:00:00.000Z"),
        ),
    )

    override suspend fun getUser(userId: String): ApiResponse<TraktUserResponse> =
        error("not used")

    override suspend fun getUserList(userId: String): ApiResponse<List<TraktPersonalListsResponse>> =
        ApiResponse.Success(lists)

    override suspend fun getListItems(
        userSlug: String,
        listId: Long,
    ): ApiResponse<List<TraktListItemResponse>> {
        itemsCalls += userSlug to listId
        itemsErrorByListId[listId]?.let { return it }
        return ApiResponse.Success(itemsByListId[listId].orEmpty())
    }

    override suspend fun createList(
        userSlug: String,
        name: String,
    ): ApiResponse<TraktCreateListResponse> = error("not used")

    override suspend fun getWatchList(
        sortBy: String,
        sortHow: String,
    ): ApiResponse<List<TraktFollowedShowResponse>> = error("not used")

    override suspend fun addShowToWatchListByTmdbId(
        tmdbId: Long,
    ): ApiResponse<TraktAddShowToListResponse> = error("not used")

    override suspend fun removeShowFromWatchListByTmdbId(
        tmdbId: Long,
    ): ApiResponse<TraktAddRemoveShowFromListResponse> = error("not used")

    override suspend fun addShowToWatchListById(
        showId: Long,
    ): ApiResponse<TraktAddShowToListResponse> = error("not used")

    override suspend fun removeShowFromWatchListById(
        showId: Long,
    ): ApiResponse<TraktAddRemoveShowFromListResponse> = error("not used")

    override suspend fun addShowsToWatchListByIds(
        showIds: List<Long>,
    ): ApiResponse<TraktAddShowToListResponse> = error("not used")

    override suspend fun removeShowsFromWatchListByIds(
        showIds: List<Long>,
    ): ApiResponse<TraktAddRemoveShowFromListResponse> = error("not used")

    override suspend fun addShowToList(
        userSlug: String,
        listId: Long,
        showId: Long,
    ): ApiResponse<TraktAddShowToListResponse> {
        addToListCalls += Triple(userSlug, listId, showId)
        return addToListResponse
    }

    override suspend fun removeShowFromList(
        userSlug: String,
        listId: Long,
        showId: Long,
    ): ApiResponse<TraktAddRemoveShowFromListResponse> {
        removeFromListCalls += Triple(userSlug, listId, showId)
        return removeFromListResponse
    }
}
