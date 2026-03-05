package com.thomaskioko.tvmaniac.presenter.home

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverShowsPresenter
import com.thomaskioko.tvmaniac.presentation.library.LibraryPresenter
import com.thomaskioko.tvmaniac.presentation.progress.ProgressPresenter
import com.thomaskioko.tvmaniac.profile.presenter.ProfilePresenter
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable

public interface HomePresenter {
    public interface Factory {
        public operator fun invoke(
            componentContext: ComponentContext,
            onShowClicked: (id: Long) -> Unit,
            onMoreShowClicked: (id: Long) -> Unit,
            onShowGenreClicked: (id: Long) -> Unit,
            onNavigateToSearch: () -> Unit,
            onSettingsClicked: () -> Unit,
            onSeasonClicked: (showTraktId: Long, seasonId: Long, seasonNumber: Long) -> Unit,
        ): HomePresenter
    }

    public val homeChildStack: StateFlow<ChildStack<*, Child>>

    public val profileAvatarUrl: StateFlow<String?>

    public fun onDiscoverClicked()
    public fun onProgressClicked()
    public fun onLibraryClicked()
    public fun onProfileClicked()
    public fun onTabClicked(config: HomeConfig)

    public sealed interface Child {
        public class Discover(public val presenter: DiscoverShowsPresenter) : Child

        public class Progress(public val presenter: ProgressPresenter) : Child

        public class Library(public val presenter: LibraryPresenter) : Child

        public class Profile(public val presenter: ProfilePresenter) : Child
    }

    @Serializable
    public sealed interface HomeConfig {
        @Serializable
        public data object Discover : HomeConfig

        @Serializable
        public data object Progress : HomeConfig

        @Serializable
        public data object Library : HomeConfig

        @Serializable
        public data object Profile : HomeConfig
    }
}
