package com.thomaskioko.tvmaniac.traktlists.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.DbTransactionRunner
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktListRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.IdsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.ListIds
import com.thomaskioko.tvmaniac.trakt.api.model.ShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddRemoveShowFromListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddShowToListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktCreateListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktFollowedShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktListItemResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktPersonalListsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserResponse
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
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
    private lateinit var repository: DefaultTraktListRepository

    @BeforeTest
    fun setUp() {
        listDao = DefaultTraktListDao(database, dispatchers)
        showDao = DefaultTraktListShowDao(database, dispatchers)
        transactionRunner = DbTransactionRunner(database)
        remoteDataSource = FakeRemoteDataSource()
        requestManager = FakeRequestManagerRepository().apply { requestValid = false }

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
                traktListItemResponse(showId = 10L),
                traktListItemResponse(showId = 20L),
            ),
        )

        repository.fetchUserLists(slug = "sean", forceRefresh = true)

        val counts = showDao.observeActiveCountByListId().first()
        counts[1L] shouldBe 2L
    }

    @Test
    fun `should preserve pending UPLOAD rows given items sync replaces synced rows`() = runTest {
        remoteDataSource.lists = listOf(traktListResponse(id = 1L, slug = "watchlist", itemCount = 1))
        remoteDataSource.itemsByListId = mapOf(
            1L to listOf(traktListItemResponse(showId = 10L)),
        )
        showDao.upsert(
            listId = 1L,
            showId = 99L,
            listedAt = "",
            pendingAction = PendingAction.UPLOAD.value,
        )

        repository.fetchUserLists(slug = "sean", forceRefresh = true)

        val entries = showDao.observeByShowId(99L).first()
        entries.size shouldBe 1
        entries[0].pendingAction shouldBe PendingAction.UPLOAD.value
        val counts = showDao.observeActiveCountByListId().first()
        counts[1L] shouldBe 2L
    }

    @Test
    fun `should preserve pending DELETE rows given items sync replaces synced rows`() = runTest {
        remoteDataSource.lists = listOf(traktListResponse(id = 1L, slug = "watchlist", itemCount = 2))
        remoteDataSource.itemsByListId = mapOf(
            1L to listOf(
                traktListItemResponse(showId = 10L),
                traktListItemResponse(showId = 20L),
            ),
        )
        showDao.upsert(
            listId = 1L,
            showId = 20L,
            listedAt = "",
            pendingAction = PendingAction.DELETE.value,
        )

        repository.fetchUserLists(slug = "sean", forceRefresh = true)

        val entries = showDao.observeByShowId(20L).first()
        entries.size shouldBe 1
        entries[0].pendingAction shouldBe PendingAction.DELETE.value
        val counts = showDao.observeActiveCountByListId().first()
        counts[1L] shouldBe 1L
    }

    @Test
    fun `should reflect synced shows in observeListsForShow given user has multiple lists`() = runTest {
        remoteDataSource.lists = listOf(
            traktListResponse(id = 1L, slug = "watchlist", itemCount = 3),
            traktListResponse(id = 2L, slug = "favorites", itemCount = 1),
        )
        remoteDataSource.itemsByListId = mapOf(
            1L to listOf(
                traktListItemResponse(showId = 10L),
                traktListItemResponse(showId = 20L),
                traktListItemResponse(showId = 30L),
            ),
            2L to listOf(traktListItemResponse(showId = 10L)),
        )

        repository.fetchUserLists(slug = "sean", forceRefresh = true)

        val lists = repository.observeListsForShow(showId = 10L).first()
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

    private fun traktListItemResponse(showId: Long) = TraktListItemResponse(
        listedAt = "2024-01-01T00:00:00.000Z",
        type = "show",
        show = ShowResponse(
            title = "Show $showId",
            year = 2024,
            ids = IdsResponse(slug = "show-$showId", trakt = showId, tmdb = showId),
        ),
    )
}

private class FakeRemoteDataSource : TraktListRemoteDataSource {
    var lists: List<TraktPersonalListsResponse> = emptyList()
    var itemsByListId: Map<Long, List<TraktListItemResponse>> = emptyMap()
    val itemsCalls: MutableList<Pair<String, Long>> = mutableListOf()

    override suspend fun getUser(userId: String): ApiResponse<TraktUserResponse> =
        error("not used")

    override suspend fun getUserList(userId: String): ApiResponse<List<TraktPersonalListsResponse>> =
        ApiResponse.Success(lists)

    override suspend fun getListItems(
        userSlug: String,
        listId: Long,
    ): ApiResponse<List<TraktListItemResponse>> {
        itemsCalls += userSlug to listId
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
    ): ApiResponse<TraktAddShowToListResponse> = error("not used")

    override suspend fun removeShowFromList(
        userSlug: String,
        listId: Long,
        showId: Long,
    ): ApiResponse<TraktAddRemoveShowFromListResponse> = error("not used")
}
