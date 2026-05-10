package com.thomaskioko.tvmaniac.presenter.home

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.asStateFlow
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.componentCoroutineScope
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.discover.nav.DiscoverRoot
import com.thomaskioko.tvmaniac.domain.user.ObserveUserProfileInteractor
import com.thomaskioko.tvmaniac.home.nav.HomeRoute
import com.thomaskioko.tvmaniac.library.nav.LibraryRoot
import com.thomaskioko.tvmaniac.navigation.BaseRoute
import com.thomaskioko.tvmaniac.navigation.MultiStackHostState
import com.thomaskioko.tvmaniac.navigation.NavDestination
import com.thomaskioko.tvmaniac.navigation.NavRoot
import com.thomaskioko.tvmaniac.navigation.NavRoute
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.navigation.RootChild
import com.thomaskioko.tvmaniac.profile.nav.ProfileRoot
import com.thomaskioko.tvmaniac.progress.nav.ProgressRoot
import dev.zacsweers.metro.Inject
import io.github.thomaskioko.codegen.annotations.DestinationKind
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.Serializable
import io.github.thomaskioko.codegen.annotations.NavDestination as NavDestinationAnno

@Serializable
public data class ProfileAvatar(val url: String? = null)

@NavDestinationAnno(
    route = HomeRoute::class,
    parentScope = ActivityScope::class,
    kind = DestinationKind.SCREEN,
)
@Inject
public class HomePresenter(
    componentContext: ComponentContext,
    private val navigator: Navigator,
    private val navDestinations: Set<NavDestination<*>>,
    private val observeUserProfileInteractor: ObserveUserProfileInteractor,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()

    private val hostStateValue: Value<MultiStackHostState<RootChild>> = navigator.buildHostNavigation(
        componentContext = this,
        initialRoot = DiscoverRoot,
        childFactory = ::child,
    )

    public val hostState: StateFlow<MultiStackHostState<RootChild>> =
        hostStateValue.asStateFlow(componentContext.componentCoroutineScope())

    public val activeRootValue: Value<NavRoot> = navigator.activeRoot

    public val activeRoot: StateFlow<NavRoot> = activeRootValue
        .asStateFlow(componentContext.componentCoroutineScope())

    public val discoverChildStackValue: Value<ChildStack<*, RootChild>> =
        hostStateValue.map { it.tabStacks.getValue(DiscoverRoot) }

    public val libraryChildStackValue: Value<ChildStack<*, RootChild>> =
        hostStateValue.map { it.tabStacks.getValue(LibraryRoot) }

    public val profileChildStackValue: Value<ChildStack<*, RootChild>> =
        hostStateValue.map { it.tabStacks.getValue(ProfileRoot) }

    public val progressChildStackValue: Value<ChildStack<*, RootChild>> =
        hostStateValue.map { it.tabStacks.getValue(ProgressRoot) }

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
        is NavRoot -> {
            val dest = navDestinations
                .filterIsInstance<NavDestination.TabRoot<*>>()
                .firstOrNull { it.matches(route) }
                ?: error("No NavDestination.TabRoot found for root: $route")
            dest.createChild(route, componentContext)
        }
        is NavRoute -> {
            val dest = navDestinations
                .firstOrNull { (it is NavDestination.Screen<*> || it is NavDestination.Overlay<*>) && it.matches(route) }
                ?: error("No NavDestination.Screen or .Overlay found for route: $route")
            when (dest) {
                is NavDestination.Screen<*> -> dest.createChild(route, componentContext)
                is NavDestination.Overlay<*> -> dest.createChild(route, componentContext)
                is NavDestination.TabRoot<*> -> error("Unreachable: filtered above")
            }
        }
    }
}
