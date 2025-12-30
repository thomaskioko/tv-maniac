package com.thomaskioko.tvmaniac.presenter.home

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverShowsPresenter
import com.thomaskioko.tvmaniac.profile.presenter.ProfilePresenter
import com.thomaskioko.tvmaniac.search.presenter.SearchShowsPresenter
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistPresenter
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable

public interface HomePresenter {
    public interface Factory {
        public operator fun invoke(
            componentContext: ComponentContext,
            onShowClicked: (id: Long) -> Unit,
            onMoreShowClicked: (id: Long) -> Unit,
            onShowGenreClicked: (id: Long) -> Unit,
            onNavigateToProfile: () -> Unit,
            onSettingsClicked: () -> Unit,
        ): HomePresenter
    }

    public val homeChildStack: StateFlow<ChildStack<*, Child>>

    public fun onDiscoverClicked()
    public fun onLibraryClicked()
    public fun onSearchClicked()
    public fun onProfileClicked()
    public fun onTabClicked(config: HomeConfig)

    public sealed interface Child {
        public class Discover(public val presenter: DiscoverShowsPresenter) : Child

        public class Watchlist(public val presenter: WatchlistPresenter) : Child

        public class Search(public val presenter: SearchShowsPresenter) : Child

        public class Profile(public val presenter: ProfilePresenter) : Child
    }

    @Serializable
    public sealed interface HomeConfig {
        @Serializable
        public data object Discover : HomeConfig

        @Serializable
        public data object Library : HomeConfig

        @Serializable
        public data object Search : HomeConfig

        @Serializable
        public data object Profile : HomeConfig
    }
}
