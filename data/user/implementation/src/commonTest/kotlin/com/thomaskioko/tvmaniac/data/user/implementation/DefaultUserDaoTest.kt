package com.thomaskioko.tvmaniac.data.user.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.user.api.model.UserProfileStats
import com.thomaskioko.tvmaniac.data.user.api.model.UserWatchTime
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import io.kotest.matchers.nulls.shouldBeNull
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
import kotlin.time.Clock

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultUserDaoTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val coroutineDispatcher = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var userDao: DefaultUserDao
    private lateinit var userStatsDao: DefaultUserStatsDao

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        userStatsDao = DefaultUserStatsDao(database, coroutineDispatcher)
        userDao = DefaultUserDao(database, userStatsDao, coroutineDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should return null when user does not exist`() = runTest {
        userDao.observeUser("non-existent").test {
            awaitItem().shouldBeNull()
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should return profile with empty stats when user exists but no stats`() = runTest {
        insertTestUser()

        userDao.observeUser("test-user").test {
            val profile = awaitItem()
            profile?.slug shouldBe "test-user"
            profile?.username shouldBe "testuser"
            profile?.fullName shouldBe "Test User"
            profile?.avatarUrl shouldBe "https://example.com/avatar.jpg"
            profile?.stats shouldBe UserProfileStats.Empty
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should return profile with stats when both exist`() = runTest {
        insertTestUser()
        userStatsDao.upsertStats(
            slug = "test-user",
            showsWatched = 10,
            episodesWatched = 100,
            minutesWatched = 6000,
        )

        userDao.observeUser("test-user").test {
            val profile = awaitItem()
            profile?.slug shouldBe "test-user"
            profile?.username shouldBe "testuser"
            profile?.stats?.showsWatched shouldBe 10
            profile?.stats?.episodesWatched shouldBe 100
            profile?.stats?.userWatchTime shouldBe UserWatchTime(
                years = 0,
                days = 4,
                hours = 4,
                minutes = 0,
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should use user background url when available`() = runTest {
        insertTestUser(backgroundUrl = "https://example.com/background.jpg")

        userDao.observeUser("test-user").test {
            val profile = awaitItem()
            profile?.backgroundUrl shouldBe "https://example.com/background.jpg"
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should use fallback backdrop when user has no background url`() = runTest {
        insertTestUser(backgroundUrl = null)
        insertTestShowWithWatchlist()

        userDao.observeUser("test-user").test {
            val profile = awaitItem()
            profile?.backgroundUrl shouldBe "/backdrop.jpg"
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should return null backdrop when no watchlist items exist`() = runTest {
        insertTestUser(backgroundUrl = null)

        userDao.observeUser("test-user").test {
            val profile = awaitItem()
            profile?.backgroundUrl.shouldBeNull()
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should update when user data changes`() = runTest {
        insertTestUser()

        userDao.observeUser("test-user").test {
            val initial = awaitItem()
            initial?.username shouldBe "testuser"

            val _ = database.userQueries.insertOrReplace(
                slug = "test-user",
                user_name = "updated-user",
                full_name = "Updated User",
                profile_picture = "https://example.com/new-avatar.jpg",
                background_url = null,
                is_me = true,
            )

            val updated = awaitItem()
            updated?.username shouldBe "updated-user"
            updated?.fullName shouldBe "Updated User"
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should update when stats change`() = runTest {
        insertTestUser()
        userStatsDao.upsertStats(
            slug = "test-user",
            showsWatched = 5,
            episodesWatched = 50,
            minutesWatched = 3000,
        )

        userDao.observeUser("test-user").test {
            val initial = awaitItem()
            initial?.stats?.showsWatched shouldBe 5

            userStatsDao.upsertStats(
                slug = "test-user",
                showsWatched = 15,
                episodesWatched = 150,
                minutesWatched = 9000,
            )

            val updated = awaitItem()
            updated?.stats?.showsWatched shouldBe 15
            updated?.stats?.episodesWatched shouldBe 150
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should return correct profile for given slug`() = runTest {
        insertTestUser(slug = "user-1", username = "user1")
        insertTestUser(slug = "user-2", username = "user2", isMe = false)

        userDao.observeUser("user-1").test {
            val profile = awaitItem()
            profile?.slug shouldBe "user-1"
            profile?.username shouldBe "user1"
            cancelAndConsumeRemainingEvents()
        }

        userDao.observeUser("user-2").test {
            val profile = awaitItem()
            profile?.slug shouldBe "user-2"
            profile?.username shouldBe "user2"
            cancelAndConsumeRemainingEvents()
        }
    }

    private fun insertTestUser(
        slug: String = "test-user",
        username: String = "testuser",
        fullName: String = "Test User",
        avatarUrl: String = "https://example.com/avatar.jpg",
        backgroundUrl: String? = null,
        isMe: Boolean = true,
    ) {
        val _ = database.userQueries.insertOrReplace(
            slug = slug,
            user_name = username,
            full_name = fullName,
            profile_picture = avatarUrl,
            background_url = backgroundUrl,
            is_me = isMe,
        )
    }

    private fun insertTestShowWithWatchlist() {
        val _ = database.tvShowQueries.upsert(
            id = Id(1),
            name = "Test Show",
            overview = "Test overview",
            language = "en",
            first_air_date = "2023-01-01",
            vote_average = 8.0,
            vote_count = 100,
            popularity = 95.0,
            genre_ids = listOf(1, 2),
            status = "Returning Series",
            episode_numbers = null,
            last_air_date = null,
            season_numbers = null,
            poster_path = "/backdrop.jpg",
            backdrop_path = "/backdrop.jpg",
        )

        val _ = database.followedShowsQueries.upsert(
            id = null,
            tmdbId = 1L,
            followedAt = Clock.System.now().toEpochMilliseconds(),
            pendingAction = "NOTHING",
            traktId = null,
        )
    }
}
