package com.thomaskioko.tvmaniac.domain.widget

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.datastore.api.AppTheme
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.subscription.api.SubscriptionFeature
import com.thomaskioko.tvmaniac.subscription.testing.FakeSubscriptionManager
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveWidgetThemeInteractorTest {

    private val testDispatcher = StandardTestDispatcher()
    private val datastoreRepository = FakeDatastoreRepository()
    private val subscriptionManager = FakeSubscriptionManager()
    private val interactor = ObserveWidgetThemeInteractor(datastoreRepository, subscriptionManager)

    @Test
    fun `should follow the app theme given no widget theme has been chosen`() = runTest(testDispatcher) {
        datastoreRepository.saveTheme(AppTheme.CRIMSON_THEME)

        interactor.flow.test {
            interactor(Unit)

            awaitItem() shouldBe AppTheme.CRIMSON_THEME
        }
    }

    @Test
    fun `should use the chosen theme given the user has access`() = runTest(testDispatcher) {
        datastoreRepository.saveTheme(AppTheme.CRIMSON_THEME)
        datastoreRepository.saveWidgetTheme(AppTheme.TERMINAL_THEME)

        interactor.flow.test {
            interactor(Unit)

            awaitItem() shouldBe AppTheme.TERMINAL_THEME
        }
    }

    @Test
    fun `should fall back to the app theme given the user has no access`() = runTest(testDispatcher) {
        datastoreRepository.saveTheme(AppTheme.AQUA_THEME)
        datastoreRepository.saveWidgetTheme(AppTheme.TERMINAL_THEME)
        subscriptionManager.setAccess(SubscriptionFeature.WidgetTheming, false)

        interactor.flow.test {
            interactor(Unit)

            awaitItem() shouldBe AppTheme.AQUA_THEME
        }
    }

    @Test
    fun `should restore the chosen theme given access is regained`() = runTest(testDispatcher) {
        datastoreRepository.saveWidgetTheme(AppTheme.SNOW_THEME)
        subscriptionManager.setAccess(SubscriptionFeature.WidgetTheming, false)

        interactor.flow.test {
            interactor(Unit)

            awaitItem() shouldBe AppTheme.SYSTEM_THEME

            subscriptionManager.setAccess(SubscriptionFeature.WidgetTheming, true)

            awaitItem() shouldBe AppTheme.SNOW_THEME
        }
    }
}
