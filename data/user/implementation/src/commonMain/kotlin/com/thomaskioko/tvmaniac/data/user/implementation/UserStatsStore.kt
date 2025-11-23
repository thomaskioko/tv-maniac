package com.thomaskioko.tvmaniac.data.user.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.store.apiFetcher
import com.thomaskioko.tvmaniac.core.store.storeBuilder
import com.thomaskioko.tvmaniac.core.store.usingDispatchers
import com.thomaskioko.tvmaniac.data.user.api.UserStatsDao
import com.thomaskioko.tvmaniac.data.user.api.model.UserProfileStats
import com.thomaskioko.tvmaniac.trakt.api.TraktUserRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserStatsResponse
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store

@Inject
public class UserStatsStore(
    private val traktUserRemoteDataSource: TraktUserRemoteDataSource,
    private val userStatsDao: UserStatsDao,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<String, UserProfileStats> by storeBuilder(
    fetcher = apiFetcher { slug ->
        traktUserRemoteDataSource.getUserStats(slug)
    },
    sourceOfTruth = SourceOfTruth.of<String, TraktUserStatsResponse, UserProfileStats>(
        reader = { slug -> userStatsDao.observeUserProfileStats(slug) },
        writer = { slug, response ->
            userStatsDao.upsertStats(
                slug = slug,
                showsWatched = response.shows.watched.toLong(),
                episodesWatched = response.episodes.watched.toLong(),
                minutesWatched = (response.episodes.minutes + response.movies.minutes).toLong(),
            )
        },
        deleteAll = { userStatsDao.deleteAll() },
    )
        .usingDispatchers(
            readDispatcher = dispatchers.databaseRead,
            writeDispatcher = dispatchers.databaseWrite,
        ),
).build()
