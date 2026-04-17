package com.thomaskioko.tvmaniac.presenter.home

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.StackNavigator
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.asStateFlow
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.componentCoroutineScope
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.domain.user.ObserveUserProfileInteractor
import com.thomaskioko.tvmaniac.home.nav.HomeRoute
import com.thomaskioko.tvmaniac.home.nav.HomeTabNavigator
import com.thomaskioko.tvmaniac.home.nav.TabChild
import com.thomaskioko.tvmaniac.home.nav.TabDestination
import com.thomaskioko.tvmaniac.home.nav.di.model.HomeConfig
import dev.zacsweers.metro.Inject
import io.github.thomaskioko.codegen.annotations.NavScreen
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.Serializable

@Serializable
public data class ProfileAvatar(val url: String? = null)

@Inject
@NavScreen(route = HomeRoute::class, parentScope = ActivityScope::class)
public class HomePresenter(
    componentContext: ComponentContext,
    homeTabNavigator: HomeTabNavigator,
    private val tabDestinations: Set<TabDestination>,
    private val observeUserProfileInteractor: ObserveUserProfileInteractor,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()

    private val navigation = StackNavigation<HomeConfig>()

    init {
        homeTabNavigator.registerNavigation(navigation)
    }

    private val homeChildStackRouter: Value<ChildStack<*, TabChild<*>>> = childStack(
        source = navigation,
        key = "HomeChildStackKey",
        initialConfiguration = HomeConfig.Discover,
        serializer = HomeConfig.serializer(),
        handleBackButton = true,
        childFactory = ::child,
    )

    public val homeChildStack: StateFlow<ChildStack<*, TabChild<*>>> =
        homeChildStackRouter.asStateFlow(componentContext.componentCoroutineScope())

    public val homeChildStackValue: Value<ChildStack<*, TabChild<*>>> = homeChildStackRouter

    public val profileAvatarUrl: StateFlow<String?> = run {
        observeUserProfileInteractor(Unit)
        observeUserProfileInteractor.flow
            .map { it?.avatarUrl }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null,
            )
    }

    public val profileAvatarUrlValue: Value<ProfileAvatar> =
        profileAvatarUrl
            .map { ProfileAvatar(url = it) }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ProfileAvatar(),
            )
            .asValue(coroutineScope)

    public fun onDiscoverClicked() {
        onTabClicked(HomeConfig.Discover)
    }

    public fun onProgressClicked() {
        onTabClicked(HomeConfig.Progress)
    }

    public fun onLibraryClicked() {
        onTabClicked(HomeConfig.Library)
    }

    public fun onProfileClicked() {
        onTabClicked(HomeConfig.Profile)
    }

    public fun onTabClicked(config: HomeConfig) {
        navigation.switchTab(config)
    }

    private inline fun <C : Any> StackNavigator<C>.switchTab(
        configuration: C,
        crossinline onComplete: () -> Unit = {},
    ) {
        navigate(
            transformer = { stack ->
                val existing = stack.find { it::class == configuration::class }
                if (existing != null) {
                    stack.filterNot { it::class == configuration::class } + existing
                } else {
                    stack + configuration
                }
            },
            onComplete = { _, _ -> onComplete() },
        )
    }

    private fun child(config: HomeConfig, componentContext: ComponentContext): TabChild<*> {
        val destination = tabDestinations.firstOrNull { it.matches(config) }
            ?: error("No TabDestination found for config: $config")
        return destination.createChild(config, componentContext)
    }
}
