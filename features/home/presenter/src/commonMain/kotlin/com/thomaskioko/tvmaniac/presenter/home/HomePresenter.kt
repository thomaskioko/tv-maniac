package com.thomaskioko.tvmaniac.presenter.home

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.asFlow
import com.thomaskioko.tvmaniac.core.base.extensions.asStateFlow
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.componentCoroutineScope
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.discover.nav.DiscoverRoot
import com.thomaskioko.tvmaniac.domain.user.ObserveUserProfileInteractor
import com.thomaskioko.tvmaniac.home.nav.HomeRoute
import com.thomaskioko.tvmaniac.home.nav.TabDestination
import com.thomaskioko.tvmaniac.library.nav.LibraryRoot
import com.thomaskioko.tvmaniac.navigation.BaseRoute
import com.thomaskioko.tvmaniac.navigation.NavDestination
import com.thomaskioko.tvmaniac.navigation.NavRoot
import com.thomaskioko.tvmaniac.navigation.NavRoute
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.navigation.RootChild
import com.thomaskioko.tvmaniac.profile.nav.ProfileRoot
import com.thomaskioko.tvmaniac.progress.nav.ProgressRoot
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
    private val navigator: Navigator,
    private val tabDestinations: Set<TabDestination>,
    private val navDestinations: Set<NavDestination>,
    private val observeUserProfileInteractor: ObserveUserProfileInteractor,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()

    private val rootStackValue: Value<ChildStack<*, NavRoot>> = navigator.buildRootStack(
        componentContext = this,
        initialRoot = DiscoverRoot,
        childFactory = { root, _ -> root },
    )

    public val activeRoot: StateFlow<NavRoot> = rootStackValue
        .asFlow()
        .map { it.active.instance }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.Eagerly,
            initialValue = rootStackValue.value.active.instance,
        )

    public val activeRootValue: Value<NavRoot> = activeRoot.asValue(coroutineScope)

    public val discoverChildStackValue: Value<ChildStack<*, RootChild>> =
        navigator.buildTabStack(componentContext = this, root = DiscoverRoot, childFactory = ::child)

    public val libraryChildStackValue: Value<ChildStack<*, RootChild>> =
        navigator.buildTabStack(componentContext = this, root = LibraryRoot, childFactory = ::child)

    public val profileChildStackValue: Value<ChildStack<*, RootChild>> =
        navigator.buildTabStack(componentContext = this, root = ProfileRoot, childFactory = ::child)

    public val progressChildStackValue: Value<ChildStack<*, RootChild>> =
        navigator.buildTabStack(componentContext = this, root = ProgressRoot, childFactory = ::child)

    public val discoverChildStack: StateFlow<ChildStack<*, RootChild>> =
        discoverChildStackValue.asStateFlow(componentContext.componentCoroutineScope())

    public val libraryChildStack: StateFlow<ChildStack<*, RootChild>> =
        libraryChildStackValue.asStateFlow(componentContext.componentCoroutineScope())

    public val profileChildStack: StateFlow<ChildStack<*, RootChild>> =
        profileChildStackValue.asStateFlow(componentContext.componentCoroutineScope())

    public val progressChildStack: StateFlow<ChildStack<*, RootChild>> =
        progressChildStackValue.asStateFlow(componentContext.componentCoroutineScope())

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
        onTabClicked(DiscoverRoot)
    }

    public fun onProgressClicked() {
        onTabClicked(ProgressRoot)
    }

    public fun onLibraryClicked() {
        onTabClicked(LibraryRoot)
    }

    public fun onProfileClicked() {
        onTabClicked(ProfileRoot)
    }

    public fun onTabClicked(root: NavRoot) {
        if (activeRoot.value == root) {
            navigator.showRoot(root)
        } else {
            navigator.switchBackStack(root)
        }
    }

    private fun child(route: BaseRoute, componentContext: ComponentContext): RootChild = when (route) {
        is NavRoot -> tabDestinations
            .firstOrNull { it.matches(route) }
            ?.createChild(route, componentContext)
            ?: error("No TabDestination found for root: $route")
        is NavRoute -> navDestinations
            .firstOrNull { it.matches(route) }
            ?.createChild(route, componentContext)
            ?: error("No NavDestination found for route: $route")
    }
}
