package com.thomaskioko.tvmaniac.domain.showdetails

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.cast.testing.FakeCastRepository
import com.thomaskioko.tvmaniac.db.CastId
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.ShowCast
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.domain.showdetails.model.Casts
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class ObserveCastInteractorTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
        main = testDispatcher,
    )
    private val castRepository = FakeCastRepository()
    private lateinit var interactor: ObserveCastInteractor

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        interactor = ObserveCastInteractor(
            castRepository = castRepository,
            dispatchers = dispatchers,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit mapped cast list given show cast data`() = runTest {
        val showCast = ShowCast(
            cast_id = Id<CastId>(1L),
            trakt_id = null,
            show_id = Id<TmdbId>(84958L),
            name = "Test Actor",
            profile_path = "/path.jpg",
            character_name = "Test Character",
        )
        castRepository.setShowCast(listOf(showCast))
        interactor(84958L)

        interactor.flow.test {
            awaitItem() shouldBe listOf(
                Casts(
                    id = 1L,
                    name = "Test Actor",
                    profileUrl = "/path.jpg",
                    characterName = "Test Character",
                ),
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should emit empty list given no cast data`() = runTest {
        castRepository.setShowCast(emptyList())
        interactor(84958L)

        interactor.flow.test {
            awaitItem() shouldBe emptyList()
            cancelAndConsumeRemainingEvents()
        }
    }
}
