package com.thomaskioko.tvmaniac.profile.presenter

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.domain.user.ObserveUserProfileInteractor
import com.thomaskioko.tvmaniac.domain.user.UpdateUserProfileData
import com.thomaskioko.tvmaniac.domain.user.model.UserProfile
import com.thomaskioko.tvmaniac.profile.presenter.ProfileAction.BackClicked
import com.thomaskioko.tvmaniac.profile.presenter.ProfileAction.LoginClicked
import com.thomaskioko.tvmaniac.profile.presenter.ProfileAction.MessageShown
import com.thomaskioko.tvmaniac.profile.presenter.ProfileAction.RefreshProfile
import com.thomaskioko.tvmaniac.profile.presenter.ProfileAction.SettingsClicked
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileInfo
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileState
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileStats
import com.thomaskioko.tvmaniac.traktauth.api.AuthError
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@ContributesBinding(ActivityScope::class, ProfilePresenter::class)
public class DefaultProfilePresenter(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onBack: () -> Unit,
    @Assisted private val onSettings: () -> Unit,
    private val traktAuthManager: TraktAuthManager,
    private val traktAuthRepository: TraktAuthRepository,
    private val updateUserProfileData: UpdateUserProfileData,
    private val logger: Logger,
    observeUserProfileInteractor: ObserveUserProfileInteractor,
    private val coroutineScope: CoroutineScope = componentContext.coroutineScope(),
) : ProfilePresenter, ComponentContext by componentContext {

    private val profileLoadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()

    init {
        observeUserProfileInteractor(Unit)
        fetchUserData()
        observeAuthState()
    }

    override val state: StateFlow<ProfileState> = combine(
        observeUserProfileInteractor.flow,
        traktAuthRepository.state,
        traktAuthRepository.authError,
        profileLoadingState.observable,
        uiMessageManager.message,
    ) { userProfile, authState, authError, isLoading, uiMessage ->
        val authenticated = authState == TraktAuthState.LOGGED_IN
        val errorMessage = authError?.toUiMessage() ?: uiMessage

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

    override fun dispatch(action: ProfileAction) {
        when (action) {
            BackClicked -> onBack()
            LoginClicked -> {
                coroutineScope.launch {
                    traktAuthManager.launchWebView()
                }
            }
            SettingsClicked -> onSettings()
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
                .collectStatus(profileLoadingState, logger, uiMessageManager)
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

private fun AuthError.toUiMessage(): UiMessage = when (this) {
    is AuthError.OAuthFailed -> UiMessage("Login failed: $message")
    AuthError.OAuthCancelled -> UiMessage("Login cancelled")
    AuthError.TokenExchangeFailed -> UiMessage("Failed to complete login. Please try again.")
    AuthError.TokenExpired -> UiMessage("Session expired. Please login again.")
    AuthError.NetworkError -> UiMessage("Network error. Please check your connection.")
    AuthError.Unknown -> UiMessage("An unexpected error occurred. Please try again.")
}

@Inject
@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class, ProfilePresenter.Factory::class)
public class DefaultProfilePresenterFactory(
    private val presenter: (
        componentContext: ComponentContext,
        onBack: () -> Unit,
        onSettings: () -> Unit,
    ) -> DefaultProfilePresenter,
) : ProfilePresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        navigateBack: () -> Unit,
        navigateToSettings: () -> Unit,
    ): ProfilePresenter = presenter(componentContext, navigateBack, navigateToSettings)
}
