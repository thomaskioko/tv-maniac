package com.thomaskioko.tvmaniac.profile.presenter

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.combine
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.data.library.model.LibraryItem
import com.thomaskioko.tvmaniac.domain.continuewatching.ObserveCompletedShowsInteractor
import com.thomaskioko.tvmaniac.domain.continuewatching.ObserveUpNextInteractor
import com.thomaskioko.tvmaniac.domain.continuewatching.ObserveWatchlistPreviewInteractor
import com.thomaskioko.tvmaniac.domain.continuewatching.model.WatchlistShowInfo
import com.thomaskioko.tvmaniac.domain.favorites.ObserveFavoritesInteractor
import com.thomaskioko.tvmaniac.domain.favorites.SyncFavoritesInteractor
import com.thomaskioko.tvmaniac.domain.library.ObserveLibraryInteractor
import com.thomaskioko.tvmaniac.domain.recentlywatched.ObserveRecentlyWatchedInteractor
import com.thomaskioko.tvmaniac.domain.traktlists.ObserveUserListsInteractor
import com.thomaskioko.tvmaniac.domain.user.ObserveUserProfileInteractor
import com.thomaskioko.tvmaniac.domain.user.UpdateUserProfileData
import com.thomaskioko.tvmaniac.domain.user.model.UserProfile
import com.thomaskioko.tvmaniac.episodes.api.model.RecentlyWatchedEpisode
import com.thomaskioko.tvmaniac.favorites.api.FavoriteShow
import com.thomaskioko.tvmaniac.i18n.PluralsResourceKey
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.profile.nav.ProfileRoot
import com.thomaskioko.tvmaniac.profile.presenter.ProfileAction.LoginClicked
import com.thomaskioko.tvmaniac.profile.presenter.ProfileAction.MessageShown
import com.thomaskioko.tvmaniac.profile.presenter.ProfileAction.RefreshProfile
import com.thomaskioko.tvmaniac.profile.presenter.ProfileAction.SettingsClicked
import com.thomaskioko.tvmaniac.profile.presenter.ProfileAction.ShowClicked
import com.thomaskioko.tvmaniac.profile.presenter.ProfileAction.ViewListsClicked
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileInfo
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileLabels
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileListItem
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileRecentItem
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileShowItem
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileState
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileStats
import com.thomaskioko.tvmaniac.profile.presenter.model.SectionState
import com.thomaskioko.tvmaniac.settings.nav.SettingsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.traktauth.api.AuthError
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import com.thomaskioko.tvmaniac.traktlists.api.TraktListEntity
import com.thomaskioko.tvmaniac.upnext.api.model.CompletedShow
import com.thomaskioko.tvmaniac.upnext.api.model.UpNextEpisode
import dev.zacsweers.metro.Inject
import io.github.thomaskioko.codegen.annotations.DestinationKind
import io.github.thomaskioko.codegen.annotations.NavDestination
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Inject
@NavDestination(
    route = ProfileRoot::class,
    parentScope = ActivityScope::class,
    kind = DestinationKind.TAB_ROOT,
)
public class ProfilePresenter(
    componentContext: ComponentContext,
    private val navigator: Navigator,
    private val localizer: Localizer,
    private val traktAuthManager: TraktAuthManager,
    private val traktAuthRepository: TraktAuthRepository,
    private val updateUserProfileData: UpdateUserProfileData,
    private val errorToStringMapper: ErrorToStringMapper,
    private val logger: Logger,
    private val syncFavoritesInteractor: SyncFavoritesInteractor,
    observeUserProfileInteractor: ObserveUserProfileInteractor,
    observeUserListsInteractor: ObserveUserListsInteractor,
    observeUpNextInteractor: ObserveUpNextInteractor,
    observeCompletedShowsInteractor: ObserveCompletedShowsInteractor,
    observeRecentlyWatchedInteractor: ObserveRecentlyWatchedInteractor,
    observeLibraryInteractor: ObserveLibraryInteractor,
    observeWatchlistPreviewInteractor: ObserveWatchlistPreviewInteractor,
    observeFavoritesInteractor: ObserveFavoritesInteractor,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()

    private val profileLoadingState = ObservableLoadingCounter()
    private val favoritesSyncState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()

    init {
        observeUserProfileInteractor(Unit)
        observeUserListsInteractor(Unit)
        observeCompletedShowsInteractor(ObserveCompletedShowsInteractor.Param())
        observeRecentlyWatchedInteractor(ObserveRecentlyWatchedInteractor.Param())
        observeLibraryInteractor(ObserveLibraryInteractor.Params(followedOnly = true))
        observeWatchlistPreviewInteractor(ObserveWatchlistPreviewInteractor.Param())
        observeFavoritesInteractor(Unit)
        fetchUserData()
        syncFavorites()
        observeAuthState()
    }

    private val sectionsFlow: Flow<ProfileSections> = combine(
        observeUserListsInteractor.flow.toSectionState { lists ->
            lists.filter { it.itemCount > 0 }.map { it.toListItem(localizer) }.toImmutableList()
        },
        observeUpNextInteractor.flow.map { it.episodes }.toSectionState { episodes ->
            episodes.take(PREVIEW_LIMIT).map { it.toShowItem() }.toImmutableList()
        },
        observeCompletedShowsInteractor.flow.toSectionState { shows ->
            shows.map { it.toShowItem() }.toImmutableList()
        },
        observeRecentlyWatchedInteractor.flow.toSectionState { episodes ->
            episodes.map { it.toRecentItem() }.toImmutableList()
        },
        observeLibraryInteractor.flow.toSectionState { items ->
            items.take(PREVIEW_LIMIT).map { it.toShowItem() }.toImmutableList()
        },
        observeWatchlistPreviewInteractor.flow.toSectionState { shows ->
            shows.map { it.toShowItem() }.toImmutableList()
        },
        observeFavoritesInteractor.flow.toSectionState { shows ->
            shows.map { it.toShowItem() }.toImmutableList()
        },
    ) { userLists, inProgress, completed, recentlyWatched, library, watchlist, favorites ->
        ProfileSections(
            userLists = userLists,
            inProgress = inProgress,
            completed = completed,
            recentlyWatched = recentlyWatched,
            library = library,
            watchlist = watchlist,
            favorites = favorites,
        )
    }

    public val state: StateFlow<ProfileState> = combine(
        observeUserProfileInteractor.flow,
        traktAuthRepository.state,
        traktAuthRepository.authError,
        profileLoadingState.observable,
        uiMessageManager.message,
        sectionsFlow,
    ) { userProfile, authState, authError, isLoading, uiMessage, sections ->
        val authenticated = authState == TraktAuthState.LOGGED_IN
        val errorMessage = authError?.toUiMessage(localizer) ?: uiMessage
        val profile = userProfile?.toPresentation()
        val displayName = profile?.fullName ?: profile?.username ?: ""

        ProfileState(
            userProfile = profile,
            isLoading = isLoading,
            errorMessage = errorMessage,
            authenticated = authenticated,
            userLists = sections.userLists,
            inProgress = sections.inProgress,
            completed = sections.completed,
            recentlyWatched = sections.recentlyWatched,
            library = sections.library,
            watchlist = sections.watchlist,
            favorites = sections.favorites,
            labels = buildLabels(displayName),
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ProfileState.DEFAULT_STATE,
    )

    public val stateValue: Value<ProfileState> = state.asValue(coroutineScope)

    public fun dispatch(action: ProfileAction) {
        when (action) {
            LoginClicked -> {
                coroutineScope.launch {
                    traktAuthManager.launchWebView()
                }
            }
            SettingsClicked -> navigator.navigateTo(SettingsRoute)
            ViewListsClicked -> {
                // TODO: Navigate to the user's lists screen. To be implemented.
            }
            RefreshProfile -> {
                fetchUserData(forceRefresh = true)
                syncFavorites(forceRefresh = true)
            }
            is ShowClicked -> navigator.navigateTo(ShowDetailsRoute(ShowDetailsParam(id = action.traktId)))
            is MessageShown -> {
                clearMessage(action.id)
                coroutineScope.launch { traktAuthRepository.setAuthError(null) }
            }
        }
    }

    private fun fetchUserData(forceRefresh: Boolean = false) {
        coroutineScope.launch {
            updateUserProfileData(UpdateUserProfileData.Params(forceRefresh = forceRefresh))
                .collectStatus(profileLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
        }
    }

    private fun syncFavorites(forceRefresh: Boolean = false) {
        coroutineScope.launch {
            syncFavoritesInteractor(SyncFavoritesInteractor.Param(forceRefresh = forceRefresh))
                .collectStatus(favoritesSyncState, logger)
        }
    }

    private fun observeAuthState() {
        coroutineScope.launch {
            traktAuthRepository.state
                .drop(1)
                .filter { it == TraktAuthState.LOGGED_IN }
                .collect {
                    fetchUserData(forceRefresh = true)
                    syncFavorites(forceRefresh = true)
                }
        }
    }

    private fun clearMessage(id: Long) {
        coroutineScope.launch {
            uiMessageManager.clearMessage(id)
        }
    }

    private fun buildLabels(displayName: String): ProfileLabels = ProfileLabels(
        title = localizer.getString(StringResourceKey.ProfileTitle),
        settingsContentDescription = localizer.getString(StringResourceKey.CdSettings),
        profilePictureContentDescription = localizer.getString(StringResourceKey.CdProfilePic, displayName),
        editButton = localizer.getString(StringResourceKey.ProfileEditButton),
        statsTitle = localizer.getString(StringResourceKey.ProfileStatsTitle),
        episodesWatched = localizer.getString(StringResourceKey.ProfileEpisodesWatched),
        showsWatched = localizer.getString(StringResourceKey.ProfileShowsWatched),
        watchTime = localizer.getString(StringResourceKey.ProfileWatchTime),
        monthsShort = localizer.getString(StringResourceKey.ProfileTimeMonthsShort),
        daysShort = localizer.getString(StringResourceKey.ProfileTimeDaysShort),
        hoursShort = localizer.getString(StringResourceKey.ProfileTimeHoursShort),
        lists = localizer.getString(StringResourceKey.ProfileLists),
        viewButton = localizer.getString(StringResourceKey.ProfileViewButton),
        userListsTitle = localizer.getString(StringResourceKey.LabelWatchlistYourLists),
        progressTitle = localizer.getString(StringResourceKey.ProfileProgressTitle),
        completedFilter = localizer.getString(StringResourceKey.ProfileFilterCompleted),
        inProgressFilter = localizer.getString(StringResourceKey.ProfileFilterInProgress),
        progressEmpty = localizer.getString(StringResourceKey.ProfileProgressEmpty),
        viewAllButton = localizer.getString(StringResourceKey.StrMore),
        retry = localizer.getString(StringResourceKey.GenericRetry),
        unauthenticatedTitle = localizer.getString(StringResourceKey.ProfileUnauthenticatedTitle),
        footerDescription = localizer.getString(StringResourceKey.ProfileFooterDescription),
        signInButton = localizer.getString(StringResourceKey.ProfileSignInButton),
        featureDiscoverTitle = localizer.getString(StringResourceKey.ProfileFeatureDiscoverTitle),
        featureDiscoverDescription = localizer.getString(StringResourceKey.ProfileFeatureDiscoverDescription),
        featureTrackTitle = localizer.getString(StringResourceKey.ProfileFeatureTrackTitle),
        featureTrackDescription = localizer.getString(StringResourceKey.ProfileFeatureTrackDescription),
        featureManageTitle = localizer.getString(StringResourceKey.ProfileFeatureManageTitle),
        featureManageDescription = localizer.getString(StringResourceKey.ProfileFeatureManageDescription),
        featureMoreTitle = localizer.getString(StringResourceKey.ProfileFeatureMoreTitle),
        featureMoreDescription = localizer.getString(StringResourceKey.ProfileFeatureMoreDescription),
    )

    private fun <S, R> Flow<List<S>>.toSectionState(
        transform: (List<S>) -> ImmutableList<R>,
    ): Flow<SectionState<R>> = map { items ->
        val mapped = transform(items)
        if (mapped.isEmpty()) SectionState.Empty else SectionState.Content(mapped)
    }.catch { emit(SectionState.Error(UiMessage(errorToStringMapper.mapError(it)))) }

    private companion object {
        private const val PREVIEW_LIMIT = 20
    }
}

private data class ProfileSections(
    val userLists: SectionState<ProfileListItem>,
    val inProgress: SectionState<ProfileShowItem>,
    val completed: SectionState<ProfileShowItem>,
    val recentlyWatched: SectionState<ProfileRecentItem>,
    val library: SectionState<ProfileShowItem>,
    val watchlist: SectionState<ProfileShowItem>,
    val favorites: SectionState<ProfileShowItem>,
)

private fun TraktListEntity.toListItem(localizer: Localizer): ProfileListItem = ProfileListItem(
    id = id,
    name = name,
    itemCount = itemCount.toInt(),
    itemCountLabel = localizer.getPlural(
        key = PluralsResourceKey.ShowCount,
        quantity = itemCount.toInt(),
        itemCount.toInt(),
    ),
    posterUrls = posterPaths.toImmutableList(),
)

private fun UpNextEpisode.toShowItem(): ProfileShowItem = ProfileShowItem(
    traktId = showTraktId,
    tmdbId = showTmdbId,
    title = showName,
    posterUrl = showPoster,
)

private fun CompletedShow.toShowItem(): ProfileShowItem = ProfileShowItem(
    traktId = showTraktId,
    tmdbId = showTmdbId,
    title = showName.orEmpty(),
    posterUrl = showPoster,
)

private fun LibraryItem.toShowItem(): ProfileShowItem = ProfileShowItem(
    traktId = traktId,
    tmdbId = tmdbId,
    title = title,
    posterUrl = posterPath,
)

private fun WatchlistShowInfo.toShowItem(): ProfileShowItem = ProfileShowItem(
    traktId = traktId,
    tmdbId = tmdbId,
    title = title.orEmpty(),
    posterUrl = posterImageUrl,
)

private fun FavoriteShow.toShowItem(): ProfileShowItem = ProfileShowItem(
    traktId = traktId,
    tmdbId = tmdbId,
    title = title,
    posterUrl = posterPath,
)

private fun RecentlyWatchedEpisode.toRecentItem(): ProfileRecentItem = ProfileRecentItem(
    traktId = showTraktId,
    tmdbId = showTmdbId,
    title = showTitle,
    posterUrl = posterPath,
    episodeLabel = "S${seasonNumber}E$episodeNumber",
)

private fun UserProfile.toPresentation(): ProfileInfo {
    val breakdown = stats.userWatchTime

    return ProfileInfo(
        slug = slug,
        username = username,
        fullName = fullName,
        avatarUrl = avatarUrl,
        stats = ProfileStats(
            showsWatched = stats.showsWatchedLabel,
            episodesWatched = stats.episodesWatchedLabel,
            years = breakdown.years,
            months = breakdown.months,
            days = breakdown.remainingDays,
            hours = breakdown.hours,
            minutes = breakdown.minutes,
        ),
        backgroundUrl = backgroundUrl,
        statsLoaded = statsLoaded,
    )
}

private fun AuthError.toUiMessage(localizer: Localizer): UiMessage = when (this) {
    is AuthError.OAuthFailed -> UiMessage(localizer.getString(StringResourceKey.ErrorLoginFailed, message))
    AuthError.OAuthCancelled -> UiMessage(localizer.getString(StringResourceKey.ErrorLoginCancelled))
    AuthError.TokenExchangeFailed -> UiMessage(localizer.getString(StringResourceKey.ErrorLoginExchange))
    AuthError.TokenExpired -> UiMessage(localizer.getString(StringResourceKey.ErrorSessionExpired))
    AuthError.NetworkError -> UiMessage(localizer.getString(StringResourceKey.ErrorNetwork))
    AuthError.NoBrowserAvailable -> UiMessage(localizer.getString(StringResourceKey.ErrorLoginNoBrowser))
    AuthError.Unknown -> UiMessage(localizer.getString(StringResourceKey.ErrorUnknown))
}
