package com.thomaskioko.tvmaniac.shared

import com.thomaskioko.trakt.service.implementation.inject.TraktComponent
import com.thomaskioko.tvmaniac.core.base.di.BaseComponent
import com.thomaskioko.tvmaniac.core.logger.inject.LoggingComponent
import com.thomaskioko.tvmaniac.core.networkutil.di.NetworkUtilPlatformComponent
import com.thomaskioko.tvmaniac.data.cast.implementation.CastComponent
import com.thomaskioko.tvmaniac.data.featuredshows.implementation.FeaturedShowsComponent
import com.thomaskioko.tvmaniac.data.popularshows.implementation.PopularShowsComponent
import com.thomaskioko.tvmaniac.data.recommendedshows.implementation.RecommendedShowsComponent
import com.thomaskioko.tvmaniac.data.showdetails.implementation.ShowDetailsComponent
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerComponent
import com.thomaskioko.tvmaniac.data.upcomingshows.implementation.UpcomingShowsComponent
import com.thomaskioko.tvmaniac.data.watchproviders.implementation.WatchProviderComponent
import com.thomaskioko.tvmaniac.datastore.implementation.DataStoreComponent
import com.thomaskioko.tvmaniac.db.DatabaseComponent
import com.thomaskioko.tvmaniac.discover.implementation.TrendingShowsComponent
import com.thomaskioko.tvmaniac.episodes.implementation.EpisodeComponent
import com.thomaskioko.tvmaniac.resourcemanager.implementation.RequestManagerComponent
import com.thomaskioko.tvmaniac.search.implementation.SearchComponent
import com.thomaskioko.tvmaniac.seasondetails.implementation.SeasonDetailsComponent
import com.thomaskioko.tvmaniac.seasons.implementation.SeasonsComponent
import com.thomaskioko.tvmaniac.shows.implementation.DiscoverComponent
import com.thomaskioko.tvmaniac.similar.implementation.SimilarShowsComponent
import com.thomaskioko.tvmaniac.tmdb.implementation.TmdbComponent
import com.thomaskioko.tvmaniac.toprated.data.implementation.TopRatedShowsComponent
import com.thomaskioko.tvmaniac.traktauth.implementation.TraktAuthComponent
import com.thomaskioko.tvmaniac.util.inject.UtilPlatformComponent
import com.thomaskioko.tvmaniac.watchlist.implementation.LibraryComponent
import me.tatarka.inject.annotations.Component
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Component
@SingleIn(AppScope::class)
abstract class SharedComponent :
  BaseComponent,
  CastComponent,
  DataStoreComponent,
  DatabaseComponent,
  DiscoverComponent,
  EpisodeComponent,
  FeaturedShowsComponent,
  LibraryComponent,
  LoggingComponent,
  NetworkUtilPlatformComponent,
  PopularShowsComponent,
  RequestManagerComponent,
  RecommendedShowsComponent,
  SeasonDetailsComponent,
  SeasonsComponent,
  SearchComponent,
  ShowDetailsComponent,
  SimilarShowsComponent,
  TmdbComponent,
  TopRatedShowsComponent,
  TrailerComponent,
  TraktAuthComponent,
  TraktComponent,
  TrendingShowsComponent,
  UtilPlatformComponent,
  UpcomingShowsComponent,
  WatchProviderComponent {
    companion object
  }
