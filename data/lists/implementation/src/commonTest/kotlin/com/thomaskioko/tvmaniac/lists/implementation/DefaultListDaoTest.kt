package com.thomaskioko.tvmaniac.lists.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.lists.api.UserListEntity
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultListDaoTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var dao: DefaultListDao

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        dao = DefaultListDao(database, dispatchers)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should exclude a pending delete row given observeAll is called`() = runTest {
        dao.upsertByTraktId(entity(traktId = 1L, name = "Watchlist"))
        val id = dao.observeAll().first().single().id

        dao.markPendingDelete(id)

        dao.observeAll().first().shouldBeEmpty()
    }

    @Test
    fun `should exclude a pending delete row given observeListsWithPosters is called`() = runTest {
        dao.upsertByTraktId(entity(traktId = 1L, name = "Watchlist"))
        val id = dao.observeAll().first().single().id

        dao.markPendingDelete(id)

        dao.observeListsWithPosters().first().shouldBeEmpty()
    }

    @Test
    fun `should leave a pending rename's name alone given upsertByTraktId runs again`() = runTest {
        dao.upsertByTraktId(entity(traktId = 1L, name = "Watchlist"))
        val id = dao.observeAll().first().single().id
        dao.rename(id = id, name = "My Watchlist")

        dao.upsertByTraktId(entity(traktId = 1L, name = "Watchlist"))

        dao.observeAll().first().single().name shouldBe "My Watchlist"
    }

    @Test
    fun `should leave a pending delete row's action alone given upsertByTraktId runs again`() = runTest {
        dao.upsertByTraktId(entity(traktId = 1L, name = "Watchlist"))
        val id = dao.observeAll().first().single().id
        dao.markPendingDelete(id)

        dao.upsertByTraktId(entity(traktId = 1L, name = "Watchlist"))

        database.listsQueries.selectById(id).executeAsOne().pending_action shouldBe PendingAction.DELETE.value
    }

    @Test
    fun `should count renames and deletes as pending changes`() = runTest {
        dao.insertLocal(name = "Pending create", createdAt = "2024-01-01")
        dao.upsertByTraktId(entity(traktId = 1L, name = "Watchlist"))
        val renameId = dao.observeAll().first().first { it.traktId == 1L }.id
        dao.rename(id = renameId, name = "My Watchlist")
        dao.upsertByTraktId(entity(traktId = 2L, name = "Favorites"))
        val deleteId = dao.observeAll().first().first { it.traktId == 2L }.id
        dao.markPendingDelete(deleteId)

        dao.countPendingChanges() shouldBe 3L
    }

    private fun entity(traktId: Long, name: String): UserListEntity = UserListEntity(
        traktId = traktId,
        slug = "slug-$traktId",
        name = name,
        description = null,
        itemCount = 0,
        createdAt = "2024-01-01",
    )
}
