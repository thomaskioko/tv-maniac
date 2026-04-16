package com.thomaskioko.tvmaniac.profile.presenter

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.domain.user.ObserveUserProfileInteractor
import com.thomaskioko.tvmaniac.domain.user.UpdateUserProfileData
import com.thomaskioko.tvmaniac.domain.user.model.UserProfile
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.profile.presenter.ProfileAction.LoginClicked
import com.thomaskioko.tvmaniac.profile.presenter.ProfileAction.MessageShown
import com.thomaskioko.tvmaniac.profile.presenter.ProfileAction.RefreshProfile
import com.thomaskioko.tvmaniac.profile.presenter.ProfileAction.SettingsClicked
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileInfo
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileState
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileStats
import com.thomaskioko.tvmaniac.settings.nav.SettingsRoute
import com.thomaskioko.tvmaniac.traktauth.api.AuthError
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Inject
public class ProfilePresenter(
    componentContext: ComponentContext,
    private val navigator: Navigator,
    private val localizer: Localizer,
    private val traktAuthManager: TraktAuthManager,
    private val traktAuthRepository: TraktAuthRepository,
    private val updateUserProfileData: UpdateUserProfileData,
    private val errorToStringMapper: ErrorToStringMapper,
    private val logger: Logger,
    observeUserProfileInteractor: ObserveUserProfileInteractor,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()

    private val profileLoadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()

    init {
        observeUserProfileInteractor(Unit)
        fetchUserData()
        observeAuthState()
    }

    public val state: StateFlow<ProfileState> = combine(
        observeUserProfileInteractor.flow,
        traktAuthRepository.state,
        traktAuthRepository.authError,
        profileLoadingState.observable,
        uiMessageManager.message,
    ) { userProfile, authState, authError, isLoading, uiMessage ->
        val authenticated = authState == TraktAuthState.LOGGED_IN
        val errorMessage = authError?.toUiMessage(localizer) ?: uiMessage

        ProfileState(
            userProfile = userProfile?.toPresentation(),
            isLoading = isLoading,
            errorMessage = errorMessage,
            authenticated = authenticated,
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
            SettingsClicked -> navigator.pushNew(SettingsRoute)
            RefreshProfile -> fetchUserData(forceRefresh = true)
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

    private fun observeAuthState() {
        coroutineScope.launch {
            traktAuthRepository.state
                .drop(1)
                .filter { it == TraktAuthState.LOGGED_IN }
                .collect { fetchUserData(forceRefresh = true) }
        }
    }

    private fun clearMessage(id: Long) {
        coroutineScope.launch {
            uiMessageManager.clearMessage(id)
        }
    }
}

private fun UserProfile.toPresentation(): ProfileInfo {
    val breakdown = stats.userWatchTime

    return ProfileInfo(
        slug = slug,
        username = username,
        fullName = fullName,
        avatarUrl = avatarUrl,
        stats = ProfileStats(
            showsWatched = stats.showsWatched,
            episodesWatched = stats.episodesWatched,
            years = breakdown.years,
            months = breakdown.months,
            days = breakdown.remainingDays,
            hours = breakdown.hours,
            minutes = breakdown.minutes,
        ),
        backgroundUrl = backgroundUrl,
    )
}

private fun AuthError.toUiMessage(localizer: Localizer): UiMessage = when (this) {
    is AuthError.OAuthFailed -> UiMessage(localizer.getString(StringResourceKey.ErrorLoginFailed, message))
    AuthError.OAuthCancelled -> UiMessage(localizer.getString(StringResourceKey.ErrorLoginCancelled))
    AuthError.TokenExchangeFailed -> UiMessage(localizer.getString(StringResourceKey.ErrorLoginExchange))
    AuthError.TokenExpired -> UiMessage(localizer.getString(StringResourceKey.ErrorSessionExpired))
    AuthError.NetworkError -> UiMessage(localizer.getString(StringResourceKey.ErrorNetwork))
    AuthError.Unknown -> UiMessage(localizer.getString(StringResourceKey.ErrorUnknown))
}
