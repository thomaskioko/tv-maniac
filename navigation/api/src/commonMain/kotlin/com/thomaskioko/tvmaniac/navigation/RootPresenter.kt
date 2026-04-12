package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.core.base.ShowFollowedNotifier
import com.thomaskioko.tvmaniac.debug.presenter.DebugPresenter
import com.thomaskioko.tvmaniac.moreshows.presentation.MoreShowsPresenter
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeDetailSheetPresenter
import com.thomaskioko.tvmaniac.presenter.home.HomePresenter
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsPresenter
import com.thomaskioko.tvmaniac.presenter.trailers.TrailersPresenter
import com.thomaskioko.tvmaniac.search.presenter.SearchShowsPresenter
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonDetailsPresenter
import com.thomaskioko.tvmaniac.settings.presenter.SettingsPresenter
import kotlinx.coroutines.flow.StateFlow

public interface RootPresenter : ShowFollowedNotifier {
    public interface Factory {
        public operator fun invoke(
            componentContext: ComponentContext,
            navigator: RootNavigator,
        ): RootPresenter
    }

    public val childStack: StateFlow<ChildStack<*, Child>>

    public val childStackValue: Value<ChildStack<*, Child>>

    public val episodeSheetSlot: StateFlow<ChildSlot<*, EpisodeDetailSheetPresenter>>

    public val episodeSheetSlotValue: Value<ChildSlot<*, EpisodeDetailSheetPresenter>>

    public val themeState: StateFlow<ThemeState>

    public val themeStateValue: Value<ThemeState>

    public val notificationPermissionState: StateFlow<NotificationPermissionState>

    public val notificationPermissionStateValue: Value<NotificationPermissionState>

    override fun onShowFollowed()

    public fun onRationaleAccepted()

    public fun onRationaleDismissed()

    public fun onNotificationPermissionResult(granted: Boolean)

    public fun onDeepLink(destination: DeepLinkDestination)

    public sealed interface Child {
        public class Home(public val presenter: HomePresenter) : Child

        public class Search(public val presenter: SearchShowsPresenter) : Child

        public class Settings(public val presenter: SettingsPresenter) : Child

        public class Debug(public val presenter: DebugPresenter) : Child

        public class ShowDetails(public val presenter: ShowDetailsPresenter) : Child

        public class SeasonDetails(public val presenter: SeasonDetailsPresenter) : Child

        public class MoreShows(public val presenter: MoreShowsPresenter) : Child

        public class Trailers(public val presenter: TrailersPresenter) : Child

        public data object GenreShows : Child
    }
}
