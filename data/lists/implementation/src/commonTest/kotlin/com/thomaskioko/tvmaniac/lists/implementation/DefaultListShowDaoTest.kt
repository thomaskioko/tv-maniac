package com.thomaskioko.tvmaniac.lists.implementation

import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.lists.api.ListShowDao
import com.thomaskioko.tvmaniac.lists.api.ListShowItem
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultListShowDaoTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var dao: ListShowDao

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        dao = DefaultListShowDao(database, dispatchers)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should return pages newest added first given a list has multiple shows`() = runTest {
        addList()
        addShow(tmdbId = 1L, name = "Show 1")
        addShow(tmdbId = 2L, name = "Show 2")
        addShow(tmdbId = 3L, name = "Show 3")
        addListShow(listId = 1L, tmdbId = 1L, listedAt = "2024-01-01")
        addListShow(listId = 1L, tmdbId = 2L, listedAt = "2024-01-02")
        addListShow(listId = 1L, tmdbId = 3L, listedAt = "2024-01-03")

        val result = dao.getPagedShows(listId = 1L).load(
            LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false),
        ) as LoadResult.Page<Int, ListShowItem>

        result.data.map { it.tmdbId } shouldBe listOf(3L, 2L, 1L)
    }

    @Test
    fun `should exclude shows pending delete given the count and the page`() = runTest {
        addList()
        addShow(tmdbId = 1L, name = "Show 1")
        addShow(tmdbId = 2L, name = "Show 2")
        addListShow(listId = 1L, tmdbId = 1L, listedAt = "2024-01-01")
        addListShow(listId = 1L, tmdbId = 2L, listedAt = "2024-01-02", pendingAction = PendingAction.DELETE.value)

        val result = dao.getPagedShows(listId = 1L).load(
            LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false),
        ) as LoadResult.Page<Int, ListShowItem>

        result.data.map { it.tmdbId } shouldBe listOf(1L)
        result.itemsAfter shouldBe 0
    }

    @Test
    fun `should start the second page where the first ended given loadSize is smaller than the total`() = runTest {
        addList()
        addShow(tmdbId = 1L, name = "Show 1")
        addShow(tmdbId = 2L, name = "Show 2")
        addShow(tmdbId = 3L, name = "Show 3")
        addListShow(listId = 1L, tmdbId = 1L, listedAt = "2024-01-01")
        addListShow(listId = 1L, tmdbId = 2L, listedAt = "2024-01-02")
        addListShow(listId = 1L, tmdbId = 3L, listedAt = "2024-01-03")
        val pagingSource = dao.getPagedShows(listId = 1L)

        val firstPage = pagingSource.load(
            LoadParams.Refresh(key = null, loadSize = 2, placeholdersEnabled = false),
        ) as LoadResult.Page<Int, ListShowItem>
        val secondPage = pagingSource.load(
            LoadParams.Append(key = firstPage.nextKey!!, loadSize = 2, placeholdersEnabled = false),
        ) as LoadResult.Page<Int, ListShowItem>

        firstPage.data.map { it.tmdbId } shouldBe listOf(3L, 2L)
        secondPage.data.map { it.tmdbId } shouldBe listOf(1L)
        secondPage.nextKey shouldBe null
    }

    private fun addList() {
        database.listsQueries.insertLocal(name = "List", createdAt = "2024-01-01T00:00:00Z")
    }

    private fun addShow(tmdbId: Long, name: String) {
        database.tvShowQueries.upsert(
            tmdb_id = Id<TmdbId>(tmdbId),
            name = name,
            overview = "$name overview",
            language = "en",
            year = "2024",
            ratings = 8.0,
            vote_count = 100,
            genres = listOf("Drama"),
            status = "Returning Series",
            episode_numbers = null,
            season_numbers = null,
            poster_path = "/$name.jpg",
            backdrop_path = null,
        )
    }

    private fun addListShow(
        listId: Long,
        tmdbId: Long,
        listedAt: String,
        pendingAction: String = PendingAction.NOTHING.value,
    ) {
        database.listShowsQueries.upsert(
            list_id = listId,
            tmdb_id = Id(tmdbId),
            listed_at = listedAt,
            pending_action = pendingAction,
        )
    }
}
