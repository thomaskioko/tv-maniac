package com.thomaskioko.tvmaniac.watchlist.presenter

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedInteractor
import com.thomaskioko.tvmaniac.domain.followedshows.FollowedShowsSyncInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ShowContentSyncInteractor
import com.thomaskioko.tvmaniac.domain.watchlist.ObserveUpNextSectionsInteractor
import com.thomaskioko.tvmaniac.domain.watchlist.ObserveWatchlistSectionsInteractor
import com.thomaskioko.tvmaniac.domain.watchlist.UpNextSectionsMapper
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.followedshows.testing.FakeFollowedShowsRepository
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasons.testing.FakeSeasonsRepository
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthRepository
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import com.thomaskioko.tvmaniac.watchlist.testing.FakeWatchlistRepository
import kotlinx.coroutines.test.UnconfinedTestDispatcher

class FakeWatchlistPresenterFactory : WatchlistPresenter.Factory {
    val repository = FakeWatchlistRepository()
    val episodeRepository = FakeEpisodeRepository()
    val dateTimeProvider = FakeDateTimeProvider()
    val traktAuthRepository = FakeTraktAuthRepository()

    val testDispatcher = UnconfinedTestDispatcher()

    private val fakeFollowedShowsRepository = FakeFollowedShowsRepository()
    private val fakeLogger = FakeLogger()

    private val coroutineDispatcher = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private val fakeMarkEpisodeWatchedInteractor = MarkEpisodeWatchedInteractor(
        episodeRepository = episodeRepository,
    )

    private val observeWatchlistSectionsInteractor = ObserveWatchlistSectionsInteractor(
        episodeRepository = episodeRepository,
        dateTimeProvider = dateTimeProvider,
    )

    private val upNextSectionsMapper = UpNextSectionsMapper(
        dateTimeProvider = dateTimeProvider,
    )

    private val observeUpNextSectionsInteractor = ObserveUpNextSectionsInteractor(
        episodeRepository = episodeRepository,
        mapper = upNextSectionsMapper,
    )

    private val showContentSyncInteractor = ShowContentSyncInteractor(
        showDetailsRepository = FakeShowDetailsRepository(),
        seasonsRepository = FakeSeasonsRepository(),
        seasonDetailsRepository = FakeSeasonDetailsRepository(),
        watchedEpisodeSyncRepository = FakeWatchedEpisodeSyncRepository(),
        datastoreRepository = FakeDatastoreRepository(),
        dispatchers = coroutineDispatcher,
    )

    private val followedShowsSyncInteractor = FollowedShowsSyncInteractor(
        followedShowsRepository = fakeFollowedShowsRepository,
        showContentSyncInteractor = showContentSyncInteractor,
        dispatchers = coroutineDispatcher,
        logger = fakeLogger,
    )

    override fun invoke(
        componentContext: ComponentContext,
        navigateToShowDetails: (showDetails: Long) -> Unit,
        navigateToSeason: (showTraktId: Long, seasonId: Long, seasonNumber: Long) -> Unit,
    ): WatchlistPresenter = DefaultWatchlistPresenter(
        componentContext = componentContext,
        navigateToShowDetails = navigateToShowDetails,
        navigateToSeason = navigateToSeason,
        repository = repository,
        followedShowsRepository = fakeFollowedShowsRepository,
        observeWatchlistSectionsInteractor = observeWatchlistSectionsInteractor,
        observeUpNextSectionsInteractor = observeUpNextSectionsInteractor,
        markEpisodeWatchedInteractor = fakeMarkEpisodeWatchedInteractor,
        followedShowsSyncInteractor = followedShowsSyncInteractor,
        traktAuthRepository = traktAuthRepository,
        logger = fakeLogger,
    )
}
