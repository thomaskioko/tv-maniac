package com.thomaskioko.tvmaniac.domain.featureflags

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.featureflags.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.flags.ContinueWatchingNitroFlag
import com.thomaskioko.tvmaniac.featureflags.flags.SimklLoginFlag
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSortDescriptor
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSource
import com.thomaskioko.tvmaniac.featureflags.testing.FakeFeatureFlagsRemoteConfig
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
internal class ObserveFeatureFlagRowsInteractorTest {

    private val testDispatcher = StandardTestDispatcher()
    private val remoteConfig = FakeFeatureFlagsRemoteConfig()
    private val nitroFlag = ContinueWatchingNitroFlag(remote = remoteConfig)
    private val simklFlag = SimklLoginFlag(remote = remoteConfig)
    private val flags: Set<FeatureFlag> = setOf(nitroFlag, simklFlag)

    private lateinit var interactor: ObserveFeatureFlagRowsInteractor

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        interactor = ObserveFeatureFlagRowsInteractor(flags = flags)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit one row per flag given default Param`() = runTest {
        interactor(ObserveFeatureFlagRowsInteractor.Param())

        interactor.flow.test {
            awaitItem().rows.size shouldBe flags.size
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit Firebase source for every flag given default Param`() = runTest {
        interactor(ObserveFeatureFlagRowsInteractor.Param())

        interactor.flow.test {
            val result = awaitItem()
            result.rows.all { it.featureFlagSource == FeatureFlagSource.Firebase } shouldBe true
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit empty rows given query without matches`() = runTest {
        interactor(ObserveFeatureFlagRowsInteractor.Param(query = "no_match_for_this_query"))

        interactor.flow.test {
            awaitItem().rows.isEmpty() shouldBe true
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit subset of rows given partial title match`() = runTest {
        interactor(ObserveFeatureFlagRowsInteractor.Param(query = simklFlag.title))

        interactor.flow.test {
            awaitItem().rows.map { it.featureFlag } shouldBe listOf(simklFlag)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit rows sorted by title given Title sort`() = runTest {
        interactor(ObserveFeatureFlagRowsInteractor.Param(sort = FeatureFlagSortDescriptor.Title))

        interactor.flow.test {
            awaitItem().rows.map { it.featureFlag } shouldBe flags.sortedByDescending { it.title }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should reverse order given ascending true`() = runTest {
        interactor(ObserveFeatureFlagRowsInteractor.Param(ascending = true))

        interactor.flow.test {
            awaitItem().rows.map { it.featureFlag } shouldBe flags.sortedBy { it.dateAdded }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should re-emit rows when params change`() = runTest {
        interactor(ObserveFeatureFlagRowsInteractor.Param())

        interactor.flow.test {
            awaitItem().params.query shouldBe ""

            interactor(ObserveFeatureFlagRowsInteractor.Param(query = "simkl"))

            awaitItem().params.query shouldBe "simkl"
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit row with Local source when remote config reports Local`() = runTest {
        interactor(ObserveFeatureFlagRowsInteractor.Param())
        remoteConfig.setSource(simklFlag.key, FeatureFlagSource.Local)

        interactor.flow.test {
            val result = awaitItem()
            result.rows shouldContain FeatureFlagRow(
                featureFlag = simklFlag,
                value = false,
                featureFlagSource = FeatureFlagSource.Local,
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should propagate Param fields into Result`() = runTest {
        val param = ObserveFeatureFlagRowsInteractor.Param(
            sort = FeatureFlagSortDescriptor.Key,
            ascending = true,
            groupByType = true,
            query = "",
        )
        interactor(param)

        interactor.flow.test {
            awaitItem().params shouldBe param
            cancelAndIgnoreRemainingEvents()
        }
    }
}
