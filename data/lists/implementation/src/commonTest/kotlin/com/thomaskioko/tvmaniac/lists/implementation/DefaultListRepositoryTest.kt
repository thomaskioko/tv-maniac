package com.thomaskioko.tvmaniac.lists.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.DbTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.shows.api.ShowToPersist
import com.thomaskioko.tvmaniac.shows.testing.FakeShowTraktIdResolver
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
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowIds
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserResponse
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultListRepositoryTest : BaseDatabaseTest() {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var listDao: DefaultListDao
    private lateinit var showDao: DefaultListShowDao
    private lateinit var transactionRunner: DatabaseTransactionRunner
    private lateinit var remoteDataSource: FakeRemoteDataSource
    private lateinit var requestManager: FakeRequestManagerRepository
    private lateinit var tvShowsDao: FakeTvShowsDao
    private lateinit var traktIdResolver: FakeShowTraktIdResolver
    private lateinit var repository: DefaultListRepository

    @BeforeTest
    fun setUp() {
        listDao = DefaultListDao(database, dispatchers)
        showDao = DefaultListShowDao(database, dispatchers)
        transactionRunner = DbTransactionRunner(database)
        remoteDataSource = FakeRemoteDataSource()
        requestManager = FakeRequestManagerRepository().apply { requestValid = false }
        tvShowsDao = FakeTvShowsDao()
        traktIdResolver = FakeShowTraktIdResolver()

        val listsStore = TraktListsStore(
            traktListDataSource = remoteDataSource,
            listDao = listDao,
            requestManagerRepository = requestManager,
            transactionRunner = transactionRunner,
            dispatchers = dispatchers,
        )
        val itemsStore = TraktListItemsStore(
            traktListRemoteDataSource = remoteDataSource,
            listDao = listDao,
            listShowDao = showDao,
            tvShowsDao = tvShowsDao,
            requestManagerRepository = requestManager,
            transactionRunner = transactionRunner,
            dispatchers = dispatchers,
        )
        repository = DefaultListRepository(
            traktListsStore = listsStore,
            traktListItemsStore = itemsStore,
            listDao = listDao,
            listShowDao = showDao,
            traktListRemoteDataSource = remoteDataSource,
            tvShowsDao = tvShowsDao,
            traktIdResolver = traktIdResolver,
            dateTimeProvider = FakeDateTimeProvider(),
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
    fun `should create a show row with title and year given an unknown tmdb id`() = runTest {
        remoteDataSource.lists = listOf(traktListResponse(id = 1L, slug = "watchlist", itemCount = 1))
        remoteDataSource.itemsByListId = mapOf(
            1L to listOf(traktListItemResponse(traktId = 10L, tmdbId = 100L)),
        )

        repository.fetchUserLists(slug = "sean", forceRefresh = true)

        val show = tvShowsDao.entries().first { it.tmdb_id.id == 100L }
        show.name shouldBe "Show 10"
        show.year shouldBe "2024"
    }

    @Test
    fun `should preserve an existing poster given the items sync backfills the show`() = runTest {
        tvShowsDao.upsert(
            ShowToPersist(
                showId = null,
                tmdbId = Id(100L),
                name = "Existing name",
                overview = "",
                ratings = 0.0,
                voteCount = 0,
                posterPath = "/existing.jpg",
            ),
        )
        remoteDataSource.lists = listOf(traktListResponse(id = 1L, slug = "watchlist", itemCount = 1))
        remoteDataSource.itemsByListId = mapOf(
            1L to listOf(traktListItemResponse(traktId = 10L, tmdbId = 100L)),
        )

        repository.fetchUserLists(slug = "sean", forceRefresh = true)

        val show = tvShowsDao.entries().first { it.tmdb_id.id == 100L }
        show.poster_path shouldBe "/existing.jpg"
        show.name shouldBe "Show 10"
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
        addShow(tmdbId = 990L, traktId = 99L)
        remoteDataSource.lists = listOf(traktListResponse(id = 1L, slug = "watchlist", itemCount = 0))
        remoteDataSource.itemsByListId = mapOf(1L to emptyList())
        repository.fetchUserLists(slug = "sean", forceRefresh = true)
        showDao.upsert(
            listId = 1L,
            tmdbId = 990L,
            listedAt = "",
            pendingAction = PendingAction.UPLOAD.value,
        )
        remoteDataSource.itemsByListId = mapOf(
            1L to listOf(traktListItemResponse(traktId = 10L, tmdbId = 100L)),
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
        addShow(tmdbId = 200L, traktId = 20L)
        remoteDataSource.lists = listOf(traktListResponse(id = 1L, slug = "watchlist", itemCount = 0))
        remoteDataSource.itemsByListId = mapOf(1L to emptyList())
        repository.fetchUserLists(slug = "sean", forceRefresh = true)
        showDao.upsert(
            listId = 1L,
            tmdbId = 200L,
            listedAt = "",
            pendingAction = PendingAction.DELETE.value,
        )
        remoteDataSource.itemsByListId = mapOf(
            1L to listOf(
                traktListItemResponse(traktId = 10L, tmdbId = 100L),
                traktListItemResponse(traktId = 20L, tmdbId = 200L),
            ),
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
        addShow(tmdbId = 100L, traktId = 10L)
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
    fun `should keep a list's id and its items given the lists sync runs again`() = runTest {
        addShow(tmdbId = 100L, traktId = 10L)
        addShow(tmdbId = 200L, traktId = 20L)
        remoteDataSource.lists = listOf(
            traktListResponse(id = 1L, slug = "watchlist", itemCount = 1),
            traktListResponse(id = 2L, slug = "favorites", itemCount = 1),
        )
        remoteDataSource.itemsByListId = mapOf(
            1L to listOf(traktListItemResponse(traktId = 10L, tmdbId = 100L)),
            2L to listOf(traktListItemResponse(traktId = 20L, tmdbId = 200L)),
        )
        repository.fetchUserLists(slug = "sean", forceRefresh = true)
        val firstSync = listDao.observeAll().first()
        val watchlistId = firstSync.first { it.traktId == 1L }.id
        val favoritesId = firstSync.first { it.traktId == 2L }.id

        remoteDataSource.lists = listOf(
            traktListResponse(id = 2L, slug = "favorites", itemCount = 1),
            traktListResponse(id = 1L, slug = "watchlist", itemCount = 1),
        )
        repository.fetchUserLists(slug = "sean", forceRefresh = true)

        val secondSync = listDao.observeAll().first()
        secondSync.first { it.traktId == 1L }.id shouldBe watchlistId
        secondSync.first { it.traktId == 2L }.id shouldBe favoritesId
        showDao.observeByShowId(100L).first().map { it.listId } shouldBe listOf(watchlistId)
        showDao.observeByShowId(200L).first().map { it.listId } shouldBe listOf(favoritesId)
    }

    @Test
    fun `should remove a list and its items given it is absent from a later sync`() = runTest {
        addShow(tmdbId = 100L, traktId = 10L)
        remoteDataSource.lists = listOf(traktListResponse(id = 1L, slug = "watchlist", itemCount = 1))
        remoteDataSource.itemsByListId = mapOf(
            1L to listOf(traktListItemResponse(traktId = 10L, tmdbId = 100L)),
        )
        repository.fetchUserLists(slug = "sean", forceRefresh = true)
        showDao.observeByShowId(100L).first().size shouldBe 1

        remoteDataSource.lists = emptyList()
        repository.fetchUserLists(slug = "sean", forceRefresh = true)

        listDao.observeAll().first().shouldBeEmpty()
        showDao.observeByShowId(100L).first().shouldBeEmpty()
    }

    @Test
    fun `should leave a local-only list untouched given a lists sync absent from the response`() = runTest {
        repository.createList(name = "Local only", traktSlug = null)
        val localListId = listDao.observeAll().first().single().id
        remoteDataSource.lists = listOf(traktListResponse(id = 1L, slug = "watchlist", itemCount = 0))
        remoteDataSource.itemsByListId = mapOf(1L to emptyList())

        repository.fetchUserLists(slug = "sean", forceRefresh = true)

        val lists = listDao.observeAll().first()
        lists.any { it.id == localListId && it.traktId == null } shouldBe true
        database.listsQueries.selectById(localListId).executeAsOne().pending_action shouldBe PendingAction.UPLOAD.value
    }

    @Test
    fun `should leave a local-only list's items untouched given an items sync runs`() = runTest {
        addShow(tmdbId = 100L, traktId = 10L)
        repository.createList(name = "Local only", traktSlug = null)
        val localListId = listDao.observeAll().first().single().id
        repository.toggleShowInList(listId = localListId, showId = 100L, isCurrentlyInList = false, traktSlug = null)
        remoteDataSource.lists = listOf(traktListResponse(id = 1L, slug = "watchlist", itemCount = 0))
        remoteDataSource.itemsByListId = mapOf(1L to emptyList())

        repository.fetchUserLists(slug = "sean", forceRefresh = true)

        val entries = showDao.observeByShowId(100L).first()
        entries.any { it.listId == localListId && it.pendingAction == PendingAction.UPLOAD.value } shouldBe true
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
    fun `should write a pending local row given no Trakt slug`() = runTest {
        repository.createList(name = "Comfort watches", traktSlug = null)

        val list = listDao.observeAll().first().single()
        list.traktId.shouldBeNull()
        database.listsQueries.selectById(list.id).executeAsOne().pending_action shouldBe PendingAction.UPLOAD.value
    }

    @Test
    fun `should push and clear the marker given a Trakt slug and a successful response`() = runTest {
        repository.createList(name = "Favorites", traktSlug = "sean")

        val list = listDao.observeAll().first().single()
        list.traktId shouldBe CREATED_LIST_TRAKT_ID
        database.listsQueries.selectById(list.id).executeAsOne().pending_action shouldBe PendingAction.NOTHING.value
    }

    @Test
    fun `should keep the pending row given the push fails`() = runTest {
        remoteDataSource.createListResponse = ApiResponse.Error.HttpError(
            code = 500,
            errorBody = null,
            errorMessage = "server error",
        )

        repository.createList(name = "Favorites", traktSlug = "sean")

        val list = listDao.observeAll().first().single()
        list.traktId.shouldBeNull()
        database.listsQueries.selectById(list.id).executeAsOne().pending_action shouldBe PendingAction.UPLOAD.value
    }

    @Test
    fun `should store trakt id and call remote with trakt id given show is added to list`() = runTest {
        addShow(tmdbId = 100L, traktId = 10L)
        addList(listId = 1L)

        repository.toggleShowInList(listId = 1L, showId = 100L, isCurrentlyInList = false, traktSlug = "sean")

        remoteDataSource.addToListCalls shouldBe listOf(Triple("sean", 1L, 10L))
        val lists = repository.observeListsForShow(showId = 100L).first()
        lists.first { it.id == 1L }.isShowInList shouldBe true
        showDao.countPendingActions() shouldBe 0L
    }

    @Test
    fun `should keep added show given items sync runs after upload`() = runTest {
        addShow(tmdbId = 100L, traktId = 10L)
        addList(listId = 1L)

        repository.toggleShowInList(listId = 1L, showId = 100L, isCurrentlyInList = false, traktSlug = "sean")

        remoteDataSource.itemsByListId = mapOf(
            1L to listOf(traktListItemResponse(traktId = 10L, tmdbId = 100L)),
        )
        repository.fetchUserLists(slug = "sean", forceRefresh = true)

        val lists = repository.observeListsForShow(showId = 100L).first()
        lists.first { it.id == 1L }.isShowInList shouldBe true
    }

    @Test
    fun `should keep UPLOAD given the push fails on add`() = runTest {
        addShow(tmdbId = 100L, traktId = 10L)
        addList(listId = 1L)
        remoteDataSource.addToListResponse = ApiResponse.Error.HttpError(
            code = 500,
            errorBody = null,
            errorMessage = "server error",
        )

        repository.toggleShowInList(listId = 1L, showId = 100L, isCurrentlyInList = false, traktSlug = "sean")

        val lists = repository.observeListsForShow(showId = 100L).first()
        lists.first { it.id == 1L }.isShowInList shouldBe true
        showDao.countPendingActions() shouldBe 1L
    }

    @Test
    fun `should delete junction entry and call remote with trakt id given show is removed from list`() = runTest {
        addShow(tmdbId = 100L, traktId = 10L)
        addList(listId = 1L)
        showDao.upsertSynced(listId = 1L, tmdbId = 100L, listedAt = "")

        repository.toggleShowInList(listId = 1L, showId = 100L, isCurrentlyInList = true, traktSlug = "sean")

        remoteDataSource.removeFromListCalls shouldBe listOf(Triple("sean", 1L, 10L))
        val lists = repository.observeListsForShow(showId = 100L).first()
        lists.first { it.id == 1L }.isShowInList shouldBe false
    }

    @Test
    fun `should keep DELETE given the push fails on remove`() = runTest {
        addShow(tmdbId = 100L, traktId = 10L)
        addList(listId = 1L)
        showDao.upsertSynced(listId = 1L, tmdbId = 100L, listedAt = "")
        remoteDataSource.removeFromListResponse = ApiResponse.Error.HttpError(
            code = 500,
            errorBody = null,
            errorMessage = "server error",
        )

        repository.toggleShowInList(listId = 1L, showId = 100L, isCurrentlyInList = true, traktSlug = "sean")

        val lists = repository.observeListsForShow(showId = 100L).first()
        lists.first { it.id == 1L }.isShowInList shouldBe false
        showDao.countPendingActions() shouldBe 1L
    }

    @Test
    fun `should write the row and stay pending given the show has no Trakt id`() = runTest {
        addList(listId = 1L)

        repository.toggleShowInList(listId = 1L, showId = 100L, isCurrentlyInList = false, traktSlug = "sean")

        val lists = repository.observeListsForShow(showId = 100L).first()
        lists.first { it.id == 1L }.isShowInList shouldBe true
        showDao.countPendingActions() shouldBe 1L
        remoteDataSource.addToListCalls.shouldBeEmpty()
    }

    @Test
    fun `should count local items given a list has no Trakt id`() = runTest {
        addShow(tmdbId = 100L, traktId = 10L)
        repository.createList(name = "Comfort watches", traktSlug = null)
        val listId = listDao.observeAll().first().single().id

        repository.toggleShowInList(listId = listId, showId = 100L, isCurrentlyInList = false, traktSlug = null)

        repository.observeLists().first().single().itemCount shouldBe 1L
    }

    @Test
    fun `should ignore pending items on local-only lists given pending changes are counted`() = runTest {
        addShow(tmdbId = 100L, traktId = 10L)
        repository.createList(name = "Comfort watches", traktSlug = null)
        val listId = listDao.observeAll().first().single().id

        repository.toggleShowInList(listId = listId, showId = 100L, isCurrentlyInList = false, traktSlug = null)

        repository.countPendingListShows() shouldBe 0L
    }

    @Test
    fun `should mark the row pending upload given a show pending delete is added again`() = runTest {
        addShow(tmdbId = 100L, traktId = 10L)
        addList(listId = 1L)
        showDao.upsertSynced(listId = 1L, tmdbId = 100L, listedAt = "")
        remoteDataSource.removeFromListResponse = ApiResponse.Error.HttpError(
            code = 500,
            errorBody = null,
            errorMessage = "server error",
        )
        repository.toggleShowInList(listId = 1L, showId = 100L, isCurrentlyInList = true, traktSlug = "sean")

        repository.toggleShowInList(listId = 1L, showId = 100L, isCurrentlyInList = false, traktSlug = null)

        showDao.observeByShowId(100L).first().single().pendingAction shouldBe PendingAction.UPLOAD.value
        repository.observeListsForShow(showId = 100L).first().first { it.id == 1L }.isShowInList shouldBe true
    }

    @Test
    fun `should write locally and never call Trakt given no Trakt slug`() = runTest {
        addShow(tmdbId = 100L, traktId = 10L)
        addList(listId = 1L)

        repository.toggleShowInList(listId = 1L, showId = 100L, isCurrentlyInList = false, traktSlug = null)

        val lists = repository.observeListsForShow(showId = 100L).first()
        lists.first { it.id == 1L }.isShowInList shouldBe true
        showDao.countPendingActions() shouldBe 1L
        remoteDataSource.addToListCalls.shouldBeEmpty()
    }

    @Test
    fun `should push a newly created list before its pending items given syncPendingLists runs`() = runTest {
        addShow(tmdbId = 100L, traktId = 10L)
        repository.createList(name = "Comfort watches", traktSlug = null)
        val listId = listDao.observeAll().first().single().id
        repository.toggleShowInList(listId = listId, showId = 100L, isCurrentlyInList = false, traktSlug = null)
        remoteDataSource.lists = emptyList()

        repository.syncPendingLists(slug = "sean")

        val list = listDao.observeAll().first().single()
        list.traktId shouldBe CREATED_LIST_TRAKT_ID
        list.slug shouldBe "favorites"
        database.listsQueries.selectById(list.id).executeAsOne().pending_action shouldBe PendingAction.NOTHING.value
        remoteDataSource.addToListCalls shouldBe listOf(Triple("sean", CREATED_LIST_TRAKT_ID, 10L))
        showDao.observeByShowId(100L).first().single().pendingAction shouldBe PendingAction.NOTHING.value
    }

    @Test
    fun `should reuse the matching Trakt list given its name matches a pending list`() = runTest {
        repository.createList(name = "Favorites", traktSlug = null)
        remoteDataSource.lists = listOf(traktListResponse(id = 77L, slug = "Favorites", itemCount = 0))

        repository.syncPendingLists(slug = "sean")

        val list = listDao.observeAll().first().single()
        list.traktId shouldBe 77L
        list.slug shouldBe "Favorites"
        database.listsQueries.selectById(list.id).executeAsOne().pending_action shouldBe PendingAction.NOTHING.value
        remoteDataSource.createListCalls.shouldBeEmpty()
    }

    @Test
    fun `should leave items pending given the list push fails`() = runTest {
        addShow(tmdbId = 100L, traktId = 10L)
        repository.createList(name = "Comfort watches", traktSlug = null)
        val listId = listDao.observeAll().first().single().id
        repository.toggleShowInList(listId = listId, showId = 100L, isCurrentlyInList = false, traktSlug = null)
        remoteDataSource.lists = emptyList()
        remoteDataSource.createListResponse = ApiResponse.Error.HttpError(
            code = 500,
            errorBody = null,
            errorMessage = "server error",
        )

        repository.syncPendingLists(slug = "sean")

        val list = listDao.observeAll().first().single()
        list.traktId.shouldBeNull()
        database.listsQueries.selectById(list.id).executeAsOne().pending_action shouldBe PendingAction.UPLOAD.value
        showDao.observeByShowId(100L).first().single().pendingAction shouldBe PendingAction.UPLOAD.value
        remoteDataSource.addToListCalls.shouldBeEmpty()
    }

    @Test
    fun `should stop pushing items given one push fails and resume on the next run`() = runTest {
        addShow(tmdbId = 100L, traktId = 10L)
        addShow(tmdbId = 200L, traktId = 20L)
        addList(listId = 1L)
        repository.toggleShowInList(listId = 1L, showId = 100L, isCurrentlyInList = false, traktSlug = null)
        repository.toggleShowInList(listId = 1L, showId = 200L, isCurrentlyInList = false, traktSlug = null)
        remoteDataSource.addToListResponse = ApiResponse.Error.HttpError(
            code = 500,
            errorBody = null,
            errorMessage = "server error",
        )

        repository.syncPendingLists(slug = "sean")

        showDao.observeByShowId(100L).first().single().pendingAction shouldBe PendingAction.UPLOAD.value
        showDao.observeByShowId(200L).first().single().pendingAction shouldBe PendingAction.UPLOAD.value
        remoteDataSource.addToListCalls shouldBe listOf(Triple("sean", 1L, 10L))

        remoteDataSource.addToListResponse = ApiResponse.Success(
            TraktAddShowToListResponse(
                added = TraktAddedShowsResponse(shows = 1),
                existing = TraktExistingShowsResponse(shows = 0),
                notFound = TraktNotFoundShowsResponse(shows = emptyList()),
                list = TraktListResponse(itemCount = 1, updateAdd = "2024-01-01T00:00:00.000Z"),
            ),
        )
        repository.syncPendingLists(slug = "sean")

        showDao.observeByShowId(100L).first().single().pendingAction shouldBe PendingAction.NOTHING.value
        showDao.observeByShowId(200L).first().single().pendingAction shouldBe PendingAction.NOTHING.value
        remoteDataSource.addToListCalls shouldBe listOf(
            Triple("sean", 1L, 10L),
            Triple("sean", 1L, 10L),
            Triple("sean", 1L, 20L),
        )
    }

    @Test
    fun `should assign distinct Trakt ids given two pending lists share a name`() = runTest {
        repository.createList(name = "Favorites", traktSlug = null)
        repository.createList(name = "Favorites", traktSlug = null)
        remoteDataSource.lists = listOf(traktListResponse(id = 77L, slug = "Favorites", itemCount = 0))

        repository.syncPendingLists(slug = "sean")

        val lists = listDao.observeAll().first()
        lists.map { it.traktId } shouldContainExactlyInAnyOrder listOf(77L, CREATED_LIST_TRAKT_ID)
        lists.forEach { list ->
            database.listsQueries.selectById(list.id).executeAsOne().pending_action shouldBe PendingAction.NOTHING.value
        }
        remoteDataSource.createListCalls shouldBe listOf("sean" to "Favorites")
    }

    @Test
    fun `should delete the row given a pending DELETE item push succeeds`() = runTest {
        addShow(tmdbId = 100L, traktId = 10L)
        addList(listId = 1L)
        showDao.upsertSynced(listId = 1L, tmdbId = 100L, listedAt = "")
        repository.toggleShowInList(listId = 1L, showId = 100L, isCurrentlyInList = true, traktSlug = null)

        repository.syncPendingLists(slug = "sean")

        showDao.observeByShowId(100L).first().shouldBeEmpty()
        remoteDataSource.removeFromListCalls shouldBe listOf(Triple("sean", 1L, 10L))
    }

    @Test
    fun `should make no remote call given a local-only list is renamed`() = runTest {
        repository.createList(name = "Comfort watches", traktSlug = null)
        val listId = listDao.observeAll().first().single().id

        repository.renameList(listId = listId, name = "Cozy watches", traktSlug = null)

        listDao.observeAll().first().single().name shouldBe "Cozy watches"
        remoteDataSource.updateListCalls.shouldBeEmpty()
    }

    @Test
    fun `should make no remote call given a local-only list is deleted`() = runTest {
        repository.createList(name = "Comfort watches", traktSlug = null)
        val listId = listDao.observeAll().first().single().id

        repository.deleteList(listId = listId, traktSlug = null)

        listDao.observeAll().first().shouldBeEmpty()
        remoteDataSource.deleteListCalls.shouldBeEmpty()
    }

    @Test
    fun `should push and clear the marker given a synced list is renamed`() = runTest {
        addList(listId = 1L)
        val listId = listDao.observeAll().first().single().id

        repository.renameList(listId = listId, name = "Renamed", traktSlug = "sean")

        remoteDataSource.updateListCalls shouldBe listOf(Triple("sean", 1L, "Renamed"))
        database.listsQueries.selectById(listId).executeAsOne().pending_action shouldBe PendingAction.NOTHING.value
        listDao.observeAll().first().single().name shouldBe "Renamed"
    }

    @Test
    fun `should stay pending given the rename push fails`() = runTest {
        addList(listId = 1L)
        val listId = listDao.observeAll().first().single().id
        remoteDataSource.updateListResponse = ApiResponse.Error.HttpError(
            code = 500,
            errorBody = null,
            errorMessage = "server error",
        )

        repository.renameList(listId = listId, name = "Renamed", traktSlug = "sean")

        database.listsQueries.selectById(listId).executeAsOne().pending_action shouldBe PendingAction.UPLOAD.value
    }

    @Test
    fun `should push the pending rename given syncPendingLists runs after the push fails`() = runTest {
        addList(listId = 1L)
        val listId = listDao.observeAll().first().single().id
        remoteDataSource.updateListResponse = ApiResponse.Error.HttpError(
            code = 500,
            errorBody = null,
            errorMessage = "server error",
        )
        repository.renameList(listId = listId, name = "Renamed", traktSlug = "sean")
        remoteDataSource.updateListResponse = ApiResponse.Success(Unit)

        repository.syncPendingLists(slug = "sean")

        database.listsQueries.selectById(listId).executeAsOne().pending_action shouldBe PendingAction.NOTHING.value
        remoteDataSource.updateListCalls shouldBe listOf(
            Triple("sean", 1L, "Renamed"),
            Triple("sean", 1L, "Renamed"),
        )
    }

    @Test
    fun `should push and remove the row given a synced list is deleted`() = runTest {
        addList(listId = 1L)
        val listId = listDao.observeAll().first().single().id

        repository.deleteList(listId = listId, traktSlug = "sean")

        remoteDataSource.deleteListCalls shouldBe listOf("sean" to 1L)
        listDao.observeAll().first().shouldBeEmpty()
    }

    @Test
    fun `should remove the row given delete returns a 404`() = runTest {
        addList(listId = 1L)
        val listId = listDao.observeAll().first().single().id
        remoteDataSource.deleteListResponse = ApiResponse.Error.HttpError(
            code = 404,
            errorBody = null,
            errorMessage = "not found",
        )

        repository.deleteList(listId = listId, traktSlug = "sean")

        listDao.observeAll().first().shouldBeEmpty()
    }

    @Test
    fun `should keep the row pending and hidden given the delete push fails with a non-404 error`() = runTest {
        addList(listId = 1L)
        val listId = listDao.observeAll().first().single().id
        remoteDataSource.deleteListResponse = ApiResponse.Error.HttpError(
            code = 500,
            errorBody = null,
            errorMessage = "server error",
        )

        repository.deleteList(listId = listId, traktSlug = "sean")

        database.listsQueries.selectById(listId).executeAsOne().pending_action shouldBe PendingAction.DELETE.value
        listDao.observeAll().first().shouldBeEmpty()
    }

    @Test
    fun `should mark the row pending and skip the remote call given no Trakt slug on delete`() = runTest {
        addList(listId = 1L)
        val listId = listDao.observeAll().first().single().id

        repository.deleteList(listId = listId, traktSlug = null)

        database.listsQueries.selectById(listId).executeAsOne().pending_action shouldBe PendingAction.DELETE.value
        remoteDataSource.deleteListCalls.shouldBeEmpty()
        listDao.observeAll().first().shouldBeEmpty()
    }

    @Test
    fun `should push the pending delete given syncPendingLists runs`() = runTest {
        addList(listId = 1L)
        val listId = listDao.observeAll().first().single().id
        repository.deleteList(listId = listId, traktSlug = null)

        repository.syncPendingLists(slug = "sean")

        remoteDataSource.deleteListCalls shouldBe listOf("sean" to 1L)
        database.listsQueries.selectById(listId).executeAsOneOrNull().shouldBeNull()
    }

    @Test
    fun `should remove a pending delete that never reached Trakt given syncPendingLists runs`() = runTest {
        val listId = listDao.insertLocal(name = "Comfort watches", createdAt = "2026-01-01T00:00:00Z")
        listDao.markPendingDelete(listId)

        repository.syncPendingLists(slug = "sean")

        remoteDataSource.deleteListCalls.shouldBeEmpty()
        database.listsQueries.selectById(listId).executeAsOneOrNull().shouldBeNull()
        listDao.countPendingChanges() shouldBe 0L
    }

    @Test
    fun `should apply a delete after an in-flight pull writes given both run at once`() = runTest {
        addList(listId = 1L)
        val listId = listDao.observeAll().first().single().id
        val gate = CompletableDeferred<Unit>()
        remoteDataSource.pullGate = gate

        val pullJob = launch { repository.fetchUserLists(slug = "sean", forceRefresh = true) }
        val deleteJob = launch { repository.deleteList(listId = listId, traktSlug = "sean") }
        gate.complete(Unit)
        pullJob.join()
        deleteJob.join()

        listDao.observeAll().first().shouldBeEmpty()
        remoteDataSource.deleteListCalls shouldBe listOf("sean" to 1L)
    }

    private fun addShow(tmdbId: Long, traktId: Long) {
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

    private suspend fun addList(listId: Long) {
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

    private companion object {
        private const val CREATED_LIST_TRAKT_ID = 34223248L
    }
}

private class FakeRemoteDataSource : TraktListRemoteDataSource {
    var lists: List<TraktPersonalListsResponse> = emptyList()
    var pullGate: CompletableDeferred<Unit>? = null
    var itemsByListId: Map<Long, List<TraktListItemResponse>> = emptyMap()
    var itemsErrorByListId: Map<Long, ApiResponse<List<TraktListItemResponse>>> = emptyMap()
    val itemsCalls: MutableList<Pair<String, Long>> = mutableListOf()
    val addToListCalls: MutableList<Triple<String, Long, Long>> = mutableListOf()
    val removeFromListCalls: MutableList<Triple<String, Long, Long>> = mutableListOf()
    val createListCalls: MutableList<Pair<String, String>> = mutableListOf()
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

    override suspend fun getUserList(userId: String): ApiResponse<List<TraktPersonalListsResponse>> {
        pullGate?.await()
        return ApiResponse.Success(lists)
    }

    override suspend fun getListItems(
        userSlug: String,
        listId: Long,
        page: Int,
        limit: Int,
    ): ApiResponse<List<TraktListItemResponse>> {
        itemsCalls += userSlug to listId
        itemsErrorByListId[listId]?.let { return it }
        return ApiResponse.Success(itemsByListId[listId].orEmpty())
    }

    var createListResponse: ApiResponse<TraktCreateListResponse> = ApiResponse.Success(
        TraktCreateListResponse(
            name = "Favorites",
            description = "",
            privacy = "private",
            ids = ListIds(trakt = 34223248, slug = "favorites"),
        ),
    )

    override suspend fun createList(
        userSlug: String,
        name: String,
    ): ApiResponse<TraktCreateListResponse> {
        createListCalls += userSlug to name
        return createListResponse
    }

    val updateListCalls: MutableList<Triple<String, Long, String>> = mutableListOf()
    val deleteListCalls: MutableList<Pair<String, Long>> = mutableListOf()
    var updateListResponse: ApiResponse<Unit> = ApiResponse.Success(Unit)
    var deleteListResponse: ApiResponse<Unit> = ApiResponse.Success(Unit)

    override suspend fun updateList(userSlug: String, listId: Long, name: String): ApiResponse<Unit> {
        updateListCalls += Triple(userSlug, listId, name)
        return updateListResponse
    }

    override suspend fun deleteList(userSlug: String, listId: Long): ApiResponse<Unit> {
        deleteListCalls += userSlug to listId
        return deleteListResponse
    }

    override suspend fun getWatchList(
        sortBy: String,
        sortHow: String,
        page: Int,
        limit: Int,
    ): ApiResponse<List<TraktFollowedShowResponse>> = error("not used")

    override suspend fun addShowsToWatchList(
        shows: List<TraktShowIds>,
    ): ApiResponse<TraktAddShowToListResponse> = error("not used")

    override suspend fun removeShowsFromWatchList(
        shows: List<TraktShowIds>,
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
