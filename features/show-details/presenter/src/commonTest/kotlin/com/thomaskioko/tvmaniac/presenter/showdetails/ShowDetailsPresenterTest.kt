package com.thomaskioko.tvmaniac.presenter.showdetails

import app.cash.turbine.test
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.root.nav.NotificationRationale
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeProviderFeatures
import com.thomaskioko.tvmaniac.core.base.coroutines.FakeAppScopeLauncher
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.notifications.testing.FakeNotificationManager
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.data.cast.testing.FakeCastRepository
import com.thomaskioko.tvmaniac.data.library.testing.FakeLibraryRepository
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.data.watchproviders.testing.FakeWatchProviderRepository
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeUnwatchedInteractor
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedInteractor
import com.thomaskioko.tvmaniac.domain.episode.ObserveShowWatchProgressInteractor
import com.thomaskioko.tvmaniac.domain.notifications.interactor.ScheduleEpisodeNotificationsInteractor
import com.thomaskioko.tvmaniac.domain.notifications.interactor.SyncCalendarInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.FetchCastInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.FetchSeasonsEpisodesInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.FetchTrailersInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.FetchWatchProvidersInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.FollowShowInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ObservableShowDetailsInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ObserveCastInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ObserveContinueTrackingInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ObserveSeasonsInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ObserveSimilarShowsInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ObserveTrailersInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ObserveWatchProvidersInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ShowDetailsInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.SyncShowMetadataInteractor
import com.thomaskioko.tvmaniac.domain.similarshows.SimilarShowsInteractor
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.followedshows.testing.FakeFollowedShowsRepository
import com.thomaskioko.tvmaniac.i18n.testing.FakeLocalizer
import com.thomaskioko.tvmaniac.navigation.testing.FakeNavigator
import com.thomaskioko.tvmaniac.presenter.showdetails.cast.ShowDetailsCastPresenter
import com.thomaskioko.tvmaniac.presenter.showdetails.cast.di.ShowDetailsCastChildGraph
import com.thomaskioko.tvmaniac.presenter.showdetails.header.ShowDetailsHeaderPresenter
import com.thomaskioko.tvmaniac.presenter.showdetails.header.di.ShowDetailsHeaderChildGraph
import com.thomaskioko.tvmaniac.presenter.showdetails.providers.ShowDetailsProvidersPresenter
import com.thomaskioko.tvmaniac.presenter.showdetails.providers.di.ShowDetailsProvidersChildGraph
import com.thomaskioko.tvmaniac.presenter.showdetails.seasonsepisodes.ShowDetailsSeasonsEpisodesPresenter
import com.thomaskioko.tvmaniac.presenter.showdetails.seasonsepisodes.di.ShowDetailsSeasonsEpisodesChildGraph
import com.thomaskioko.tvmaniac.presenter.showdetails.similar.ShowDetailsSimilarPresenter
import com.thomaskioko.tvmaniac.presenter.showdetails.similar.di.ShowDetailsSimilarChildGraph
import com.thomaskioko.tvmaniac.presenter.showdetails.trailers.ShowDetailsTrailersPresenter
import com.thomaskioko.tvmaniac.presenter.showdetails.trailers.di.ShowDetailsTrailersChildGraph
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasons.testing.FakeSeasonsEpisodesSyncRepository
import com.thomaskioko.tvmaniac.seasons.testing.FakeSeasonsRepository
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.similar.testing.FakeSimilarShowsRepository
import com.thomaskioko.tvmaniac.trailers.testing.FakeTrailerRepository
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import com.thomaskioko.tvmaniac.util.testing.FakeFormatterUtil
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class ShowDetailsPresenterTest {

    private val testDispatcher = StandardTestDispatcher()
    private val appCoroutineScope = CoroutineScope(testDispatcher + SupervisorJob())
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private val showDetailsRepository = FakeShowDetailsRepository()
    private val seasonDetailsRepository = FakeSeasonDetailsRepository()
    private val seasonsEpisodesSyncRepository = FakeSeasonsEpisodesSyncRepository()
    private val watchedEpisodeSyncRepository = FakeWatchedEpisodeSyncRepository()
    private val watchProvidersRepository = FakeWatchProviderRepository()
    private val followedShowsRepository = FakeFollowedShowsRepository()
    private val episodeRepository = FakeEpisodeRepository()
    private val datastoreRepository = FakeDatastoreRepository()
    private val notificationManager = FakeNotificationManager()
    private val seasonsRepository = FakeSeasonsRepository()
    private val castRepository = FakeCastRepository()
    private val trailerRepository = FakeTrailerRepository()
    private val similarShowsRepository = FakeSimilarShowsRepository()
    private val accountManager = FakeAccountManager()
    private val localizer = FakeLocalizer()
    private val formatterUtil = FakeFormatterUtil()
    private val dateTimeProvider = FakeDateTimeProvider()
    private val navigator = FakeNavigator()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        notificationManager.reset()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should expose empty aggregated state given children have no data`() = runTest {
        val presenter = buildPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()

            val state = expectMostRecentItem()
            state.isRefreshing shouldBe false
            state.message.shouldBeNull()
        }
    }

    @Test
    fun `should surface the first child message given a child fetch fails`() = runTest {
        showDetailsRepository.setFetchError(IllegalStateException("boom"))

        val presenter = buildPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()

            val withMessage = expectMostRecentItem()
            withMessage.message?.message shouldBe "boom"

            presenter.dispatch(ShowDetailsMessageShown(withMessage.message!!.id))
            testDispatcher.scheduler.advanceUntilIdle()

            expectMostRecentItem().message.shouldBeNull()
        }
    }

    @Test
    fun `should re-trigger child fetches given reload dispatched`() = runTest {
        val presenter = buildPresenter()
        testDispatcher.scheduler.advanceUntilIdle()
        showDetailsRepository.clearInvocations()
        watchProvidersRepository.clearFetchInvocations()

        presenter.dispatch(ShowDetailsReload)
        testDispatcher.scheduler.advanceUntilIdle()

        showDetailsRepository.fetchInvocations().last().forceRefresh shouldBe true
        watchProvidersRepository.fetchInvocations().last().forceRefresh shouldBe true
    }

    @Test
    fun `should navigate back given back clicked dispatched`() = runTest {
        val presenter = buildPresenter()
        testDispatcher.scheduler.advanceUntilIdle()

        presenter.dispatch(ShowDetailsBackClicked)

        navigator.navigateBackCount shouldBe 1
    }

    private fun buildPresenter(): ShowDetailsPresenter =
        ShowDetailsPresenter(
            componentContext = DefaultComponentContext(lifecycle = LifecycleRegistry()),
            param = ShowDetailsParam(showId = SHOW_ID),
            headerGraphFactory = headerGraphFactory(),
            seasonsEpisodesGraphFactory = seasonsEpisodesGraphFactory(),
            castGraphFactory = castGraphFactory(),
            providersGraphFactory = providersGraphFactory(),
            trailersGraphFactory = trailersGraphFactory(),
            similarGraphFactory = similarGraphFactory(),
            navigator = navigator,
        )

    private fun headerGraphFactory() = object : ShowDetailsHeaderChildGraph.Factory {
        override fun createShowDetailsHeaderGraph(componentContext: ComponentContext): ShowDetailsHeaderChildGraph =
            object : ShowDetailsHeaderChildGraph {
                override val showDetailsHeaderFactory = ShowDetailsHeaderPresenter.Factory { showId, forceRefresh ->
                    buildHeader(componentContext, showId, forceRefresh)
                }
            }
    }

    private fun seasonsEpisodesGraphFactory() = object : ShowDetailsSeasonsEpisodesChildGraph.Factory {
        override fun createShowDetailsSeasonsEpisodesGraph(
            componentContext: ComponentContext,
        ): ShowDetailsSeasonsEpisodesChildGraph =
            object : ShowDetailsSeasonsEpisodesChildGraph {
                override val showDetailsSeasonsEpisodesFactory =
                    ShowDetailsSeasonsEpisodesPresenter.Factory { showId, forceRefresh ->
                        buildSeasonsEpisodes(componentContext, showId, forceRefresh)
                    }
            }
    }

    private fun castGraphFactory() = object : ShowDetailsCastChildGraph.Factory {
        override fun createShowDetailsCastGraph(componentContext: ComponentContext): ShowDetailsCastChildGraph =
            object : ShowDetailsCastChildGraph {
                override val showDetailsCastFactory = ShowDetailsCastPresenter.Factory { showId, forceRefresh ->
                    buildCast(componentContext, showId, forceRefresh)
                }
            }
    }

    private fun providersGraphFactory() = object : ShowDetailsProvidersChildGraph.Factory {
        override fun createShowDetailsProvidersGraph(componentContext: ComponentContext): ShowDetailsProvidersChildGraph =
            object : ShowDetailsProvidersChildGraph {
                override val showDetailsProvidersFactory =
                    ShowDetailsProvidersPresenter.Factory { showId, forceRefresh ->
                        buildProviders(componentContext, showId, forceRefresh)
                    }
            }
    }

    private fun trailersGraphFactory() = object : ShowDetailsTrailersChildGraph.Factory {
        override fun createShowDetailsTrailersGraph(componentContext: ComponentContext): ShowDetailsTrailersChildGraph =
            object : ShowDetailsTrailersChildGraph {
                override val showDetailsTrailersFactory = ShowDetailsTrailersPresenter.Factory { showId, forceRefresh ->
                    buildTrailers(componentContext, showId, forceRefresh)
                }
            }
    }

    private fun similarGraphFactory() = object : ShowDetailsSimilarChildGraph.Factory {
        override fun createShowDetailsSimilarGraph(componentContext: ComponentContext): ShowDetailsSimilarChildGraph =
            object : ShowDetailsSimilarChildGraph {
                override val showDetailsSimilarFactory = ShowDetailsSimilarPresenter.Factory { showId, forceRefresh ->
                    buildSimilar(componentContext, showId, forceRefresh)
                }
            }
    }

    private fun buildHeader(
        componentContext: ComponentContext,
        showId: Long,
        forceRefresh: Boolean,
    ): ShowDetailsHeaderPresenter {
        val notificationRationale = object : NotificationRationale {
            override suspend fun showIfNeeded() = Unit
        }
        return ShowDetailsHeaderPresenter(
            componentContext = componentContext,
            showId = showId,
            forceRefresh = forceRefresh,
            navigator = navigator,
            notificationRationale = notificationRationale,
            followedShowsRepository = followedShowsRepository,
            followShowInteractor = FollowShowInteractor(
                followedShowsRepository = followedShowsRepository,
                libraryRepository = FakeLibraryRepository(),
                syncShowMetadataInteractor = SyncShowMetadataInteractor(
                    showDetailsRepository = showDetailsRepository,
                    seasonDetailsRepository = seasonDetailsRepository,
                    watchProviderRepository = watchProvidersRepository,
                    dispatchers = dispatchers,
                ),
                appScopeLauncher = FakeAppScopeLauncher(scope = appCoroutineScope),
            ),
            showDetailsInteractor = ShowDetailsInteractor(
                showDetailsRepository = showDetailsRepository,
                dispatchers = dispatchers,
            ),
            observableShowDetailsInteractor = ObservableShowDetailsInteractor(
                showDetailsRepository = showDetailsRepository,
                formatterUtil = formatterUtil,
                dispatchers = dispatchers,
            ),
            syncCalendarInteractor = SyncCalendarInteractor(
                episodeRepository = episodeRepository,
                dateTimeProvider = dateTimeProvider,
                activeProviderFeatures = { FakeProviderFeatures(supportsCalendar = true) },
                logger = FakeLogger(),
                dispatchers = dispatchers,
            ),
            scheduleEpisodeNotificationsInteractor = ScheduleEpisodeNotificationsInteractor(
                datastoreRepository = datastoreRepository,
                episodeRepository = episodeRepository,
                notificationManager = notificationManager,
                localizer = localizer,
                dateTimeProvider = dateTimeProvider,
                logger = FakeLogger(),
                dispatchers = dispatchers,
            ),
            notificationManager = notificationManager,
            accountManager = accountManager,
            activeProviderFeatures = { FakeProviderFeatures(supportsLists = true) },
            localizer = localizer,
            errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
            logger = FakeLogger(),
        )
    }

    private fun buildSeasonsEpisodes(
        componentContext: ComponentContext,
        showId: Long,
        forceRefresh: Boolean,
    ): ShowDetailsSeasonsEpisodesPresenter =
        ShowDetailsSeasonsEpisodesPresenter(
            componentContext = componentContext,
            showId = showId,
            forceRefresh = forceRefresh,
            observeSeasonsInteractor = ObserveSeasonsInteractor(
                seasonsRepository = seasonsRepository,
                episodeRepository = episodeRepository,
                dispatchers = dispatchers,
            ),
            observeShowWatchProgressInteractor = ObserveShowWatchProgressInteractor(
                episodeRepository = episodeRepository,
            ),
            observeContinueTrackingInteractor = ObserveContinueTrackingInteractor(
                seasonDetailsRepository = seasonDetailsRepository,
                followedShowsRepository = followedShowsRepository,
                dispatchers = dispatchers,
            ),
            fetchSeasonsEpisodesInteractor = FetchSeasonsEpisodesInteractor(
                seasonsEpisodesSyncRepository = seasonsEpisodesSyncRepository,
                seasonDetailsRepository = seasonDetailsRepository,
                watchedEpisodeSyncRepository = watchedEpisodeSyncRepository,
                dispatchers = dispatchers,
            ),
            markEpisodeWatchedInteractor = MarkEpisodeWatchedInteractor(
                episodeRepository = episodeRepository,
            ),
            markEpisodeUnwatchedInteractor = MarkEpisodeUnwatchedInteractor(
                episodeRepository = episodeRepository,
            ),
            navigator = navigator,
            accountManager = accountManager,
            errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
            logger = FakeLogger(),
        )

    private fun buildCast(
        componentContext: ComponentContext,
        showId: Long,
        forceRefresh: Boolean,
    ): ShowDetailsCastPresenter =
        ShowDetailsCastPresenter(
            componentContext = componentContext,
            showId = showId,
            forceRefresh = forceRefresh,
            observeCastInteractor = ObserveCastInteractor(
                castRepository = castRepository,
                dispatchers = dispatchers,
            ),
            fetchCastInteractor = FetchCastInteractor(
                castRepository = castRepository,
                dispatchers = dispatchers,
            ),
            accountManager = accountManager,
            errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
            logger = FakeLogger(),
        )

    private fun buildProviders(
        componentContext: ComponentContext,
        showId: Long,
        forceRefresh: Boolean,
    ): ShowDetailsProvidersPresenter =
        ShowDetailsProvidersPresenter(
            componentContext = componentContext,
            showId = showId,
            forceRefresh = forceRefresh,
            observeWatchProvidersInteractor = ObserveWatchProvidersInteractor(
                watchProviderRepository = watchProvidersRepository,
                dispatchers = dispatchers,
            ),
            fetchWatchProvidersInteractor = FetchWatchProvidersInteractor(
                watchProviderRepository = watchProvidersRepository,
                dispatchers = dispatchers,
            ),
            accountManager = accountManager,
            errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
            logger = FakeLogger(),
        )

    private fun buildTrailers(
        componentContext: ComponentContext,
        showId: Long,
        forceRefresh: Boolean,
    ): ShowDetailsTrailersPresenter =
        ShowDetailsTrailersPresenter(
            componentContext = componentContext,
            showId = showId,
            forceRefresh = forceRefresh,
            observeTrailersInteractor = ObserveTrailersInteractor(
                trailerRepository = trailerRepository,
                dispatchers = dispatchers,
            ),
            fetchTrailersInteractor = FetchTrailersInteractor(
                trailerRepository = trailerRepository,
                dispatchers = dispatchers,
            ),
            navigator = navigator,
            accountManager = accountManager,
            errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
            logger = FakeLogger(),
        )

    private fun buildSimilar(
        componentContext: ComponentContext,
        showId: Long,
        forceRefresh: Boolean,
    ): ShowDetailsSimilarPresenter =
        ShowDetailsSimilarPresenter(
            componentContext = componentContext,
            showId = showId,
            forceRefresh = forceRefresh,
            observeSimilarShowsInteractor = ObserveSimilarShowsInteractor(
                similarShowsRepository = similarShowsRepository,
                dispatchers = dispatchers,
            ),
            similarShowsInteractor = SimilarShowsInteractor(
                similarShowsRepository = similarShowsRepository,
                dispatchers = dispatchers,
            ),
            navigator = navigator,
            accountManager = accountManager,
            errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
            logger = FakeLogger(),
        )

    private companion object {
        private const val SHOW_ID = 84958L
    }
}
