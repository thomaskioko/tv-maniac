package com.thomaskioko.tvmaniac.shared

import com.thomaskioko.trakt.service.implementation.inject.TraktComponent
import com.thomaskioko.tvmaniac.data.category.implementation.CategoryComponent
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerComponent
import com.thomaskioko.tvmaniac.datastore.implementation.DataStoreComponent
import com.thomaskioko.tvmaniac.db.DatabaseComponent
import com.thomaskioko.tvmaniac.episodeimages.implementation.EpisodeImageComponent
import com.thomaskioko.tvmaniac.episodes.implementation.EpisodeComponent
import com.thomaskioko.tvmaniac.profile.implementation.ProfileComponent
import com.thomaskioko.tvmaniac.profilestats.implementation.StatsComponent
import com.thomaskioko.tvmaniac.resourcemanager.implementation.RequestManagerComponent
import com.thomaskioko.tvmaniac.seasondetails.implementation.SeasonDetailsComponent
import com.thomaskioko.tvmaniac.seasons.implementation.SeasonsComponent
import com.thomaskioko.tvmaniac.showimages.implementation.ShowImagesComponent
import com.thomaskioko.tvmaniac.shows.implementation.DiscoverComponent
import com.thomaskioko.tvmaniac.similar.implementation.SimilarShowsComponent
import com.thomaskioko.tvmaniac.tmdb.implementation.TmdbComponent
import com.thomaskioko.tvmaniac.traktauth.implementation.TraktAuthenticationComponent
import com.thomaskioko.tvmaniac.util.inject.LoggingComponent
import com.thomaskioko.tvmaniac.util.inject.UtilPlatformComponent
import com.thomaskioko.tvmaniac.watchlist.implementation.LibraryComponent

abstract class SharedComponent :
    CategoryComponent,
    DataStoreComponent,
    DatabaseComponent,
    DiscoverComponent,
    EpisodeComponent,
    EpisodeImageComponent,
    LibraryComponent,
    LoggingComponent,
    ProfileComponent,
    RequestManagerComponent,
    SeasonDetailsComponent,
    SeasonsComponent,
    ShowImagesComponent,
    SimilarShowsComponent,
    StatsComponent,
    TmdbComponent,
    TrailerComponent,
    TraktAuthenticationComponent,
    TraktComponent,
    UtilPlatformComponent
