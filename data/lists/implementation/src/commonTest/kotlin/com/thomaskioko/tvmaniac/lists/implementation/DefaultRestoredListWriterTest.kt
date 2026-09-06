package com.thomaskioko.tvmaniac.lists.implementation

import com.thomaskioko.tvmaniac.accountmanager.testing.FakeProviderFeatures
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.data.backup.api.model.BackupList
import com.thomaskioko.tvmaniac.data.backup.api.model.BackupListShow
import com.thomaskioko.tvmaniac.data.user.testing.FakeUserRepository
import com.thomaskioko.tvmaniac.lists.api.ListDao
import com.thomaskioko.tvmaniac.lists.api.PendingUploadList
import com.thomaskioko.tvmaniac.lists.api.UserListEntity
import com.thomaskioko.tvmaniac.lists.testing.FakeListRepository
import com.thomaskioko.tvmaniac.shows.testing.FakeShowTraktIdResolver
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class DefaultRestoredListWriterTest {

    private val listRepository = FakeListRepository()
    private val listDao = RecordingListDao()
    private val userRepository = FakeUserRepository()
    private val traktIdResolver = FakeShowTraktIdResolver()
    private var supportsLists = true

    private val writer = DefaultRestoredListWriter(
        listRepository = listRepository,
        listDao = listDao,
        userRepository = userRepository,
        activeProviderFeatures = { FakeProviderFeatures(supportsLists = supportsLists) },
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
    fun `should push to Trakt given a signed in user whose provider syncs lists`() = runTest {
        listDao.createOnUpsertOf(name = LIST_NAME, id = LIST_ID)

        writer.restoreLists(listOf(backupList()))

        listRepository.lastCreateTraktSlug() shouldBe userRepository.getCurrentUser()?.slug
        listRepository.lastToggleTraktSlug() shouldBe userRepository.getCurrentUser()?.slug
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
    fun `should restore local lists given no user is signed in`() = runTest {
        userRepository.setUserProfile(null)
        listDao.createOnUpsertOf(name = LIST_NAME, id = LIST_ID)

        val restored = writer.restoreLists(listOf(backupList()))

        restored shouldBe 1
        listRepository.createdListNames() shouldBe listOf(LIST_NAME)
        listRepository.lastCreateTraktSlug() shouldBe null
        listRepository.toggledShows() shouldBe listOf(LIST_ID to BREAKING_BAD_TMDB_ID)
    }

    @Test
    fun `should create the list locally given the active provider does not sync lists`() = runTest {
        supportsLists = false
        listDao.createOnUpsertOf(name = LIST_NAME, id = LIST_ID)

        val restored = writer.restoreLists(listOf(backupList()))

        restored shouldBe 1
        listRepository.lastCreateTraktSlug() shouldBe null
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

    private fun entity(id: Long, name: String): UserListEntity = UserListEntity(
        id = id,
        slug = "comfort-watches",
        name = name,
        description = null,
        itemCount = 0,
        createdAt = LISTED_AT,
    )

    private inner class RecordingListDao : ListDao {
        private val lists = MutableStateFlow<List<UserListEntity>>(emptyList())
        private var pendingName: String? = null
        private var pendingId: Long = 0

        fun setLists(entities: List<UserListEntity>) {
            lists.value = entities
        }

        fun createOnUpsertOf(name: String, id: Long) {
            pendingName = name
            pendingId = id
        }

        override fun observeAll(): Flow<List<UserListEntity>> {
            pendingName?.let { name ->
                if (listRepository.createdListNames().contains(name)) {
                    lists.value = listOf(entity(id = pendingId, name = name))
                    pendingName = null
                }
            }
            return lists
        }

        override fun observeListsWithPosters(): Flow<List<UserListEntity>> = lists

        override fun upsertByTraktId(entity: UserListEntity) {
            lists.value = lists.value.filterNot { it.traktId == entity.traktId } + entity
        }

        override fun insertLocal(name: String, createdAt: String): Long = pendingId

        override fun markSynced(id: Long, traktId: Long, slug: String?) {
            lists.value = lists.value.map { if (it.id == id) it.copy(traktId = traktId, slug = slug) else it }
        }

        override fun getTraktId(id: Long): Long? = lists.value.firstOrNull { it.id == id }?.traktId

        override fun selectIdsByTraktId(): Map<Long, Long> =
            lists.value.mapNotNull { list -> list.traktId?.let { it to list.id } }.toMap()

        override fun selectPendingUploadLists(): List<PendingUploadList> = emptyList()

        override fun countPendingUploads(): Long = 0L

        override fun deleteById(id: Long) {
            lists.value = lists.value.filterNot { it.id == id }
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
