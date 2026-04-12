package com.thomaskioko.tvmaniac.navigation

import com.thomaskioko.tvmaniac.debug.presenter.DebugPresenter
import com.thomaskioko.tvmaniac.moreshows.presentation.MoreShowsPresenter
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeDetailSheetPresenter
import com.thomaskioko.tvmaniac.presenter.home.HomePresenter
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsPresenter
import com.thomaskioko.tvmaniac.presenter.trailers.TrailersPresenter
import com.thomaskioko.tvmaniac.search.presenter.SearchShowsPresenter
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonDetailsPresenter
import com.thomaskioko.tvmaniac.settings.presenter.SettingsPresenter

public sealed interface RootScreen : RootChild {
  public class Home(public val presenter: HomePresenter) : RootScreen

  public class Search(public val presenter: SearchShowsPresenter) : RootScreen

  public class Settings(public val presenter: SettingsPresenter) : RootScreen

  public class Debug(public val presenter: DebugPresenter) : RootScreen

  public class ShowDetails(public val presenter: ShowDetailsPresenter) : RootScreen

  public class SeasonDetails(public val presenter: SeasonDetailsPresenter) : RootScreen

  public class MoreShows(public val presenter: MoreShowsPresenter) : RootScreen

  public class Trailers(public val presenter: TrailersPresenter) : RootScreen

  public data object GenreShows : RootScreen
}
