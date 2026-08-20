package com.thomaskioko.tvmaniac.traktlists.implementation

import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.data.backup.api.BackupList
import com.thomaskioko.tvmaniac.data.backup.api.BackupListShow
import com.thomaskioko.tvmaniac.data.user.testing.FakeUserRepository
import com.thomaskioko.tvmaniac.shows.testing.FakeShowTraktIdResolver
import com.thomaskioko.tvmaniac.traktlists.api.TraktListDao
import com.thomaskioko.tvmaniac.traktlists.api.TraktListEntity
import com.thomaskioko.tvmaniac.traktlists.testing.FakeTraktListRepository
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class TraktRestoredListWriterTest {

    private val listRepository = FakeTraktListRepository()
    private val listDao = RecordingTraktListDao()
    private val userRepository = FakeUserRepository()
    private val traktIdResolver = FakeShowTraktIdResolver()

    private val writer = TraktRestoredListWriter(
        traktListRepository = listRepository,
        traktListDao = listDao,
        userRepository = userRepository,
        traktIdResolver = traktIdResolver,
        logger = FakeLogger(),
    )

    @Test
    fun `should create the list and add its members given a signed in user`() = runTest {
        listDao.createOnUpsertOf(name = LIST_NAME, id = LIST_ID)

        val restored = writer.restoreLists(listOf(backupList()))

        restored shouldBe 1
        listRepository.createdListNames() shouldBe listOf(LIST_NAME)
        listRepository.toggledShows() shouldBe listOf(LIST_ID to BREAKING_BAD_TMDB_ID)
    }

    @Test
    fun `should resolve trakt ids before adding members`() = runTest {
        listDao.createOnUpsertOf(name = LIST_NAME, id = LIST_ID)

        writer.restoreLists(listOf(backupList()))

        traktIdResolver.requestedIds() shouldBe listOf(listOf(BREAKING_BAD_TMDB_ID))
    }

    @Test
    fun `should reuse a list given the account already has one with that name`() = runTest {
        listDao.setLists(listOf(entity(id = LIST_ID, name = LIST_NAME)))

        val restored = writer.restoreLists(listOf(backupList()))

        restored shouldBe 1
        listRepository.createdListNames().shouldBeEmpty()
        listRepository.toggledShows() shouldBe listOf(LIST_ID to BREAKING_BAD_TMDB_ID)
    }

    @Test
    fun `should restore nothing given no user is signed in`() = runTest {
        userRepository.setUserProfile(null)

        val restored = writer.restoreLists(listOf(backupList()))

        restored shouldBe 0
        listRepository.createdListNames().shouldBeEmpty()
    }

    @Test
    fun `should skip a list given the provider refuses to create it`() = runTest {
        listRepository.setCreateListFailure(IllegalStateException("boom"))

        val restored = writer.restoreLists(listOf(backupList()))

        restored shouldBe 0
        listRepository.toggledShows().shouldBeEmpty()
    }

    @Test
    fun `should keep the list given one member cannot be added`() = runTest {
        listDao.createOnUpsertOf(name = LIST_NAME, id = LIST_ID)
        listRepository.setToggleFailure(IllegalArgumentException("no trakt id"))

        val restored = writer.restoreLists(listOf(backupList()))

        restored shouldBe 1
    }

    @Test
    fun `should restore nothing given the file carries no lists`() = runTest {
        val restored = writer.restoreLists(emptyList())

        restored shouldBe 0
        traktIdResolver.requestedIds().shouldBeEmpty()
    }

    private fun backupList(): BackupList = BackupList(
        name = LIST_NAME,
        shows = listOf(BackupListShow(tmdbId = BREAKING_BAD_TMDB_ID, listedAt = LISTED_AT)),
    )

    private fun entity(id: Long, name: String): TraktListEntity = TraktListEntity(
        id = id,
        slug = "comfort-watches",
        name = name,
        description = null,
        itemCount = 0,
        createdAt = LISTED_AT,
    )

    private inner class RecordingTraktListDao : TraktListDao {
        private val lists = MutableStateFlow<List<TraktListEntity>>(emptyList())
        private var pendingName: String? = null
        private var pendingId: Long = 0

        fun setLists(entities: List<TraktListEntity>) {
            lists.value = entities
        }

        fun createOnUpsertOf(name: String, id: Long) {
            pendingName = name
            pendingId = id
        }

        override fun observeAll(): Flow<List<TraktListEntity>> {
            pendingName?.let { name ->
                if (listRepository.createdListNames().contains(name)) {
                    lists.value = listOf(entity(id = pendingId, name = name))
                    pendingName = null
                }
            }
            return lists
        }

        override fun observeListsWithPosters(): Flow<List<TraktListEntity>> = lists

        override fun upsert(entity: TraktListEntity) {
            lists.value = lists.value + entity
        }

        override fun deleteAll() {
            lists.value = emptyList()
        }
    }

    private companion object {
        private const val LIST_NAME = "Comfort watches"
        private const val LIST_ID = 42L
        private const val BREAKING_BAD_TMDB_ID = 1396L
        private const val LISTED_AT = "2026-01-01T00:00:00Z"
    }
}
