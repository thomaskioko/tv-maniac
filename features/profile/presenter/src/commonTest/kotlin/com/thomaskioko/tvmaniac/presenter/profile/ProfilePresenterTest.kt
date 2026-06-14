package com.thomaskioko.tvmaniac.presenter.profile

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.api.AuthError
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAuthManager
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeProviderFeatures
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.data.library.model.LibraryItem
import com.thomaskioko.tvmaniac.data.library.testing.FakeLibraryRepository
import com.thomaskioko.tvmaniac.data.user.api.model.UserProfile
import com.thomaskioko.tvmaniac.data.user.api.model.UserProfileStats
import com.thomaskioko.tvmaniac.data.user.api.model.UserWatchTime
import com.thomaskioko.tvmaniac.data.user.testing.FakeUserRepository
import com.thomaskioko.tvmaniac.data.user.testing.createTestProfile
import com.thomaskioko.tvmaniac.domain.continuewatching.ObserveCompletedShowsInteractor
import com.thomaskioko.tvmaniac.domain.continuewatching.ObserveUpNextInteractor
import com.thomaskioko.tvmaniac.domain.continuewatching.ObserveWatchlistPreviewInteractor
import com.thomaskioko.tvmaniac.domain.favorites.ObserveFavoritesInteractor
import com.thomaskioko.tvmaniac.domain.favorites.SyncFavoritesInteractor
import com.thomaskioko.tvmaniac.domain.library.ObserveLibraryInteractor
import com.thomaskioko.tvmaniac.domain.recentlywatched.ObserveRecentlyWatchedInteractor
import com.thomaskioko.tvmaniac.domain.traktlists.ObserveUserListsInteractor
import com.thomaskioko.tvmaniac.domain.user.ObserveUserProfileInteractor
import com.thomaskioko.tvmaniac.domain.user.UpdateUserProfileData
import com.thomaskioko.tvmaniac.episodes.api.model.RecentlyWatchedEpisode
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.favorites.api.FavoriteShow
import com.thomaskioko.tvmaniac.favorites.testing.FakeFavoritesRepository
import com.thomaskioko.tvmaniac.featureflags.testing.FakeFeatureFlag
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.testing.FakeLocalizer
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.navigation.testing.NoOpNavigator
import com.thomaskioko.tvmaniac.navigation.testing.TestNavigator
import com.thomaskioko.tvmaniac.navigation.testing.test
import com.thomaskioko.tvmaniac.profile.presenter.ProfileAction
import com.thomaskioko.tvmaniac.profile.presenter.ProfilePresenter
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileInfo
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileListItem
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileRecentItem
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileShowItem
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileState
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileStats
import com.thomaskioko.tvmaniac.profile.presenter.model.SectionState
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.traktlists.api.TraktListEntity
import com.thomaskioko.tvmaniac.traktlists.testing.FakeTraktListRepository
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.upnext.testing.FakeUpNextRepository
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class ProfilePresenterTest {

    private val lifecycle = LifecycleRegistry()
    private val testDispatcher = StandardTestDispatcher()
    private val userRepository = FakeUserRepository()
    private val accountManager = FakeAccountManager()
    private val authManager = FakeAuthManager()
    private val simklAuthManager = FakeAuthManager(AccountProvider.SIMKL)
    private val simklFlag = FakeFeatureFlag(initial = false)
    private val traktListRepository = FakeTraktListRepository()
    private val upNextRepository = FakeUpNextRepository()
    private val episodeRepository = FakeEpisodeRepository()
    private val libraryRepository = FakeLibraryRepository()
    private val favoritesRepository = FakeFavoritesRepository()
    private val logger = FakeLogger()
    private val testProfile = createTestProfile(
        stats = UserProfileStats(
            showsWatched = 10,
            episodesWatched = 100,
            showsWatchedLabel = "10",
            episodesWatchedLabel = "100",
            userWatchTime = UserWatchTime(
                years = 0,
                days = 4,
                hours = 4,
                minutes = 0,
            ),
        ),
    )
    private val testDispatchers = AppCoroutineDispatchers(
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
        main = testDispatcher,
    )

    private val observeUserProfileInteractor = ObserveUserProfileInteractor(
        userRepository = userRepository,
        accountManager = accountManager,
    )

    private val updateUserProfileData = UpdateUserProfileData(
        userRepository = userRepository,
        traktListRepository = traktListRepository,
        activeProviderFeatures = { FakeProviderFeatures(supportsLists = true) },
        dispatchers = testDispatchers,
    )

    private lateinit var presenter: ProfilePresenter

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        presenter = createPresenter()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit default state when initialized`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe ProfileState.DEFAULT_STATE
        }
    }

    @Test
    fun `should load profile when user is authenticated`() = runTest {
        accountManager.setActiveProvider(AccountProvider.TRAKT)
        userRepository.setUserProfile(testProfile)

        presenter.state.test {
            awaitItem() shouldBe ProfileState.DEFAULT_STATE

            val loadedState = awaitItem()
            loadedState.userProfile shouldBe createExpectedProfileInfo(testProfile)
            loadedState.authenticated shouldBe true
            loadedState.activeProvider shouldBe AccountProvider.TRAKT
            loadedState.isLoading shouldBe false
        }
    }

    @Test
    fun `should show loading and not the sign in screen when login starts`() = runTest {
        presenter.state.test {
            var state = awaitItem()
            while (state.showLoading || state.authenticated) {
                state = awaitItem()
            }
            state.authenticated shouldBe false
            state.showLoading shouldBe false

            presenter.dispatch(ProfileAction.LoginClicked(AccountProvider.TRAKT))

            var loading = awaitItem()
            while (!loading.showLoading) {
                loading = awaitItem()
            }
            loading.showLoading shouldBe true
            loading.authenticated shouldBe false
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should update state when auth state changes to logged in`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe ProfileState.DEFAULT_STATE

            userRepository.setUserProfile(testProfile)
            accountManager.setActiveProvider(AccountProvider.TRAKT)

            val loadedState = awaitItem()
            loadedState.userProfile shouldBe createExpectedProfileInfo(testProfile)
            loadedState.authenticated shouldBe true
            loadedState.isLoading shouldBe false
        }
    }

    @Test
    fun `should update profile when user data changes`() = runTest {
        accountManager.setActiveProvider(AccountProvider.TRAKT)
        userRepository.setUserProfile(testProfile)

        presenter.state.test {
            awaitItem() shouldBe ProfileState.DEFAULT_STATE

            val loadedState = awaitItem()
            loadedState.userProfile shouldBe createExpectedProfileInfo(testProfile)
            loadedState.authenticated shouldBe true
            loadedState.isLoading shouldBe false

            val updatedProfile = testProfile.copy(
                username = "updated-user",
                stats = testProfile.stats.copy(showsWatched = 20, showsWatchedLabel = "20"),
            )
            userRepository.setUserProfile(updatedProfile)

            val updatedState = awaitItem()
            updatedState.userProfile?.username shouldBe "updated-user"
            updatedState.userProfile?.stats?.showsWatched shouldBe "20"
        }
    }

    @Test
    fun `should load profile after authentication`() = runTest {
        userRepository.setUserProfile(null)

        val testPresenter = createPresenter()

        testPresenter.state.test {
            awaitItem() shouldBe ProfileState.DEFAULT_STATE

            accountManager.setActiveProvider(AccountProvider.TRAKT)
            userRepository.setUserProfile(testProfile)

            val loadedState = awaitItem()
            loadedState.userProfile shouldBe createExpectedProfileInfo(testProfile)
            loadedState.authenticated shouldBe true
            loadedState.isLoading shouldBe false
        }
    }

    @Test
    fun `should hide loading when user logs out`() = runTest {
        accountManager.setActiveProvider(AccountProvider.TRAKT)
        userRepository.setUserProfile(testProfile)

        presenter.state.test {
            awaitItem() shouldBe ProfileState.DEFAULT_STATE

            val loadedState = awaitItem()
            loadedState.userProfile shouldBe createExpectedProfileInfo(testProfile)
            loadedState.authenticated shouldBe true
            loadedState.showLoading shouldBe false

            accountManager.setActiveProvider(null)
            userRepository.setUserProfile(null)

            var loggedOutState = awaitItem()
            while (loggedOutState.userProfile != null || loggedOutState.authenticated) {
                loggedOutState = awaitItem()
            }

            loggedOutState.userProfile shouldBe null
            loggedOutState.authenticated shouldBe false
            loggedOutState.activeProvider shouldBe null
            loggedOutState.showLoading shouldBe false
        }
    }

    @Test
    fun `should reset loading state on successful authentication`() = runTest {
        userRepository.setUserProfile(null)
        accountManager.setActiveProvider(null)

        val testPresenter = createPresenter()

        testPresenter.state.test {
            awaitItem() shouldBe ProfileState.DEFAULT_STATE

            accountManager.setActiveProvider(AccountProvider.TRAKT)
            userRepository.setUserProfile(testProfile)

            val authenticatedState = awaitItem()
            authenticatedState.authenticated shouldBe true
            authenticatedState.showLoading shouldBe false
            authenticatedState.userProfile shouldBe createExpectedProfileInfo(testProfile)
        }
    }

    @Test
    fun `should keep showing loading given profile present but stats not yet loaded`() = runTest {
        userRepository.holdStatsFetch()
        accountManager.setActiveProvider(AccountProvider.TRAKT)
        userRepository.setUserProfile(testProfile.copy(statsLoaded = false))

        val testPresenter = createPresenter()

        testPresenter.state.test {
            runCurrent()
            val loadingState = expectMostRecentItem()
            loadingState.userProfile shouldNotBe null
            loadingState.showLoading shouldBe true

            userRepository.setUserProfile(testProfile.copy(statsLoaded = true))
            runCurrent()
            expectMostRecentItem().showLoading shouldBe false

            userRepository.releaseStatsFetch()
        }
    }

    @Test
    fun `should expect and surface stats given simkl active`() = runTest {
        userRepository.holdStatsFetch()
        accountManager.setActiveProvider(AccountProvider.SIMKL)
        userRepository.setUserProfile(testProfile.copy(statsLoaded = false))

        val testPresenter = createPresenter()

        testPresenter.state.test {
            runCurrent()
            val loadingState = expectMostRecentItem()
            loadingState.userProfile shouldNotBe null
            loadingState.userProfile?.awaitingStats shouldBe true
            loadingState.showLoading shouldBe true

            userRepository.setUserProfile(testProfile.copy(statsLoaded = true))
            runCurrent()
            val loadedState = expectMostRecentItem()
            loadedState.userProfile?.stats shouldNotBe null
            loadedState.userProfile?.awaitingStats shouldBe false
            loadedState.showLoading shouldBe false

            userRepository.releaseStatsFetch()
        }
    }

    @Test
    fun `should not show loading after auth error is dismissed`() = runTest {
        userRepository.setUserProfile(null)
        accountManager.setActiveProvider(null)

        val testPresenter = createPresenter()

        testPresenter.state.test {
            awaitItem() shouldBe ProfileState.DEFAULT_STATE

            accountManager.setAuthErrorValue(AuthError.OAuthCancelled)

            val errorState = awaitItem()
            errorState.errorMessage?.message shouldBe FakeLocalizer().getString(StringResourceKey.ErrorLoginCancelled)
            errorState.showLoading shouldBe false

            val messageId = errorState.errorMessage!!.id
            testPresenter.dispatch(ProfileAction.MessageShown(messageId))

            val dismissedState = awaitItem()
            dismissedState.errorMessage shouldBe null
            dismissedState.showLoading shouldBe false
            dismissedState.authenticated shouldBe false
        }
    }

    @Test
    fun `should surface no-browser error when sign-in cannot launch`() = runTest {
        userRepository.setUserProfile(null)
        accountManager.setActiveProvider(null)

        val testPresenter = createPresenter()

        testPresenter.state.test {
            awaitItem() shouldBe ProfileState.DEFAULT_STATE

            accountManager.setAuthErrorValue(AuthError.NoBrowserAvailable)

            val errorState = awaitItem()
            errorState.errorMessage?.message shouldBe FakeLocalizer().getString(StringResourceKey.ErrorLoginNoBrowser)
            errorState.showLoading shouldBe false
            errorState.authenticated shouldBe false
        }
    }

    @Test
    fun `should map each section to content when data is available`() = runTest {
        accountManager.setActiveProvider(AccountProvider.TRAKT)
        userRepository.setUserProfile(testProfile)
        traktListRepository.setLists(listOf(createListEntity()))
        upNextRepository.setNextEpisodesForWatchlist(listOf(createNextEpisode()))
        episodeRepository.setRecentlyWatched(listOf(createRecentlyWatched()))
        libraryRepository.setLibraryItems(listOf(createLibraryItem()))
        favoritesRepository.setFavorites(listOf(createFavorite()))

        presenter.state.test {
            awaitItem() shouldBe ProfileState.DEFAULT_STATE

            val loaded = awaitItem()

            val userLists = loaded.userLists.shouldBeInstanceOf<SectionState.Content<ProfileListItem>>()
            userLists.items shouldBe listOf(
                ProfileListItem(
                    id = 1,
                    name = "Favorites",
                    itemCount = 12,
                    itemCountLabel = "12 shows",
                    posterUrls = persistentListOf("/list-poster.jpg"),
                ),
            )

            val inProgress = loaded.inProgress.shouldBeInstanceOf<SectionState.Content<ProfileShowItem>>()
            inProgress.items.first().showId shouldBe 1L
            inProgress.items.first().title shouldBe "Breaking Bad"

            val recent = loaded.recentlyWatched.shouldBeInstanceOf<SectionState.Content<ProfileRecentItem>>()
            recent.items shouldBe listOf(
                ProfileRecentItem(
                    showId = 1L,
                    tmdbId = 1L,
                    title = "Breaking Bad",
                    posterUrl = "/poster.jpg",
                    episodeLabel = "S1E5",
                ),
            )

            val library = loaded.library.shouldBeInstanceOf<SectionState.Content<ProfileShowItem>>()
            library.items.first().showId shouldBe 1L

            val watchlist = loaded.watchlist.shouldBeInstanceOf<SectionState.Content<ProfileShowItem>>()
            watchlist.items.first().showId shouldBe 1L

            val favorites = loaded.favorites.shouldBeInstanceOf<SectionState.Content<ProfileShowItem>>()
            favorites.items shouldBe listOf(
                ProfileShowItem(showId = 1L, tmdbId = 2L, title = "Breaking Bad", posterUrl = "/poster.jpg"),
            )
        }
    }

    @Test
    fun `should map sections to empty when no data is available`() = runTest {
        accountManager.setActiveProvider(AccountProvider.TRAKT)
        userRepository.setUserProfile(testProfile)

        presenter.state.test {
            awaitItem() shouldBe ProfileState.DEFAULT_STATE

            val loaded = awaitItem()
            loaded.userLists shouldBe SectionState.Empty
            loaded.inProgress shouldBe SectionState.Empty
            loaded.recentlyWatched shouldBe SectionState.Empty
            loaded.library shouldBe SectionState.Empty
            loaded.watchlist shouldBe SectionState.Empty
            loaded.favorites shouldBe SectionState.Empty
        }
    }

    @Test
    fun `should map a single section to error without collapsing the others`() = runTest {
        accountManager.setActiveProvider(AccountProvider.TRAKT)
        userRepository.setUserProfile(testProfile)
        favoritesRepository.setObserveError(IllegalStateException("favorites boom"))
        libraryRepository.setLibraryItems(listOf(createLibraryItem()))

        presenter.state.test {
            awaitItem() shouldBe ProfileState.DEFAULT_STATE

            val loaded = awaitItem()
            val error = loaded.favorites.shouldBeInstanceOf<SectionState.Error>()
            error.message.message shouldBe "favorites boom"

            loaded.library.shouldBeInstanceOf<SectionState.Content<ProfileShowItem>>()
        }
    }

    @Test
    fun `should navigate to show details given show is clicked`() = runTest {
        val navigator = TestNavigator()
        val testPresenter = createPresenter(navigator = navigator)

        navigator.test {
            testPresenter.dispatch(ProfileAction.ShowClicked(showId = 5L))
            awaitNavigateTo(ShowDetailsRoute(ShowDetailsParam(showId = 5L)))
        }
    }

    @Test
    fun `should force refresh favorites given refresh action`() = runTest {
        advanceUntilIdle()
        favoritesRepository.clearInvocations()

        presenter.dispatch(ProfileAction.RefreshProfile)
        advanceUntilIdle()

        favoritesRepository.syncInvocations() shouldBe
            listOf(FakeFavoritesRepository.SyncInvocation(forceRefresh = true))
    }

    @Test
    fun `should launch the chosen provider given a non default provider`() = runTest {
        var traktLaunches = 0
        var simklLaunches = 0
        authManager.setOnLaunchWebView { traktLaunches += 1 }
        simklAuthManager.setOnLaunchWebView { simklLaunches += 1 }

        presenter.dispatch(ProfileAction.LoginClicked(AccountProvider.SIMKL))
        advanceUntilIdle()

        simklLaunches shouldBe 1
        traktLaunches shouldBe 0
    }

    @Test
    fun `should expose only the trakt option given the simkl flag is off`() = runTest {
        presenter.state.test {
            runCurrent()
            expectMostRecentItem().authProviders.map { it.provider } shouldBe listOf(AccountProvider.TRAKT)
        }
    }

    @Test
    fun `should expose both provider options given the simkl flag is on`() = runTest {
        simklFlag.value = true

        presenter.state.test {
            runCurrent()
            expectMostRecentItem().authProviders.map { it.provider } shouldBe
                listOf(AccountProvider.TRAKT, AccountProvider.SIMKL)
        }
    }

    private fun createExpectedProfileInfo(profile: UserProfile): ProfileInfo {
        return ProfileInfo(
            slug = profile.slug,
            username = profile.username,
            fullName = profile.fullName,
            avatarUrl = profile.avatarUrl,
            backgroundUrl = profile.backgroundUrl,
            stats = ProfileStats(
                showsWatched = profile.stats.showsWatchedLabel,
                episodesWatched = profile.stats.episodesWatchedLabel,
                years = 0,
                months = 0,
                days = 4,
                hours = 4,
                minutes = 0,
            ),
        )
    }

    private fun createListEntity(): TraktListEntity = TraktListEntity(
        id = 1,
        slug = "favorites",
        name = "Favorites",
        description = null,
        itemCount = 12,
        createdAt = "2024-01-01",
        posterPaths = listOf("/list-poster.jpg"),
    )

    private fun createNextEpisode(): NextEpisodeWithShow = NextEpisodeWithShow(
        showId = 1L,
        showName = "Breaking Bad",
        showPoster = "/poster.jpg",
        showStatus = "Ended",
        showYear = "2008",
        episodeId = 10L,
        episodeName = "Episode",
        seasonId = 5L,
        seasonNumber = 1L,
        episodeNumber = 2L,
        runtime = 45L,
        stillPath = "/still.jpg",
        overview = "Overview",
        firstAired = null,
        lastWatchedAt = null,
        seasonCount = 5,
        episodeCount = 62,
        watchedCount = 3,
        totalCount = 62,
    )

    private fun createRecentlyWatched(): RecentlyWatchedEpisode = RecentlyWatchedEpisode(
        showId = 1L,
        showTitle = "Breaking Bad",
        posterPath = "/poster.jpg",
        seasonNumber = 1L,
        episodeNumber = 5L,
        episodeTitle = "Gray Matter",
        watchedAt = 1000L,
    )

    private fun createLibraryItem(): LibraryItem = LibraryItem(
        showId = 1L,
        tmdbId = 2L,
        title = "Breaking Bad",
        posterPath = "/poster.jpg",
        status = "Ended",
        year = "2008",
        rating = 9.0,
        genres = null,
        seasonCount = 5,
        episodeCount = 62,
        watchedCount = 3,
        totalCount = 62,
        lastWatchedAt = null,
        followedAt = 1000L,
        isFollowed = true,
    )

    private fun createFavorite(): FavoriteShow = FavoriteShow(
        showId = 1L,
        tmdbId = 2L,
        title = "Breaking Bad",
        posterPath = "/poster.jpg",
        year = "2008",
    )

    private fun createPresenter(navigator: Navigator = NoOpNavigator()): ProfilePresenter {
        return ProfilePresenter(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            navigator = navigator,
            localizer = FakeLocalizer(),
            authManagers = mapOf(
                AccountProvider.TRAKT to authManager,
                AccountProvider.SIMKL to simklAuthManager,
            ),
            simklLoginFlag = simklFlag,
            accountManager = accountManager,
            updateUserProfileData = updateUserProfileData,
            errorToStringMapper = { it.message ?: "Test error" },
            logger = logger,
            syncFavoritesInteractor = SyncFavoritesInteractor(favoritesRepository, testDispatchers),
            observeUserProfileInteractor = observeUserProfileInteractor,
            observeUserListsInteractor = ObserveUserListsInteractor(traktListRepository),
            observeUpNextInteractor = ObserveUpNextInteractor(upNextRepository),
            observeCompletedShowsInteractor = ObserveCompletedShowsInteractor(upNextRepository),
            observeRecentlyWatchedInteractor = ObserveRecentlyWatchedInteractor(episodeRepository),
            observeLibraryInteractor = ObserveLibraryInteractor(libraryRepository),
            observeWatchlistPreviewInteractor = ObserveWatchlistPreviewInteractor(upNextRepository),
            observeFavoritesInteractor = ObserveFavoritesInteractor(favoritesRepository),
        )
    }
}
