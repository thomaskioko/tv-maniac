package com.thomaskioko.tvmaniac.shows.implementation

import com.thomaskioko.tvmaniac.category.api.cache.CategoryCache
import com.thomaskioko.tvmaniac.category.api.model.Category
import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.db.ShowsByCategory
import com.thomaskioko.tvmaniac.shows.api.ShowsDao
import com.thomaskioko.tvmaniac.trakt.api.TraktShowsRemoteDataSource
import com.thomaskioko.tvmaniac.util.model.AppCoroutineScope
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreBuilder

@Inject
class DiscoverShowsStore(
    private val remoteDataSource: TraktShowsRemoteDataSource,
    private val showsDao: ShowsDao,
    private val categoryCache: CategoryCache,
    private val mapper: DiscoverResponseMapper,
    private val scope: AppCoroutineScope,
) : Store<Category, List<ShowsByCategory>> by StoreBuilder.from<Category, List<ShowsByCategory>, List<ShowsByCategory>>(
    fetcher = Fetcher.of { category ->
        when (category) {
            Category.POPULAR -> {
                val apiResponse = remoteDataSource.getPopularShows()
                mapper.showResponseToCacheList(category, apiResponse)
            }
            Category.TRENDING -> {
                val apiResponse = remoteDataSource.getTrendingShows()
                mapper.responseToEntityList(category, apiResponse)
            }
            Category.ANTICIPATED -> {
                val apiResponse = remoteDataSource.getAnticipatedShows()
                mapper.responseToEntityList(category, apiResponse)
            }
            Category.RECOMMENDED -> {
                val apiResponse = remoteDataSource.getRecommendedShows()
                mapper.responseToEntityList(category, apiResponse)
            }
        }
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { category -> showsDao.observeCachedShows(category.id) },
        writer = { category, list ->

            val shows = list.map {
                Show(
                    id = it.id,
                    tmdb_id = it.tmdb_id,
                    title = it.title,
                    overview = it.overview,
                    language = it.language,
                    year = it.year,
                    rating = it.rating,
                    status = it.status,
                    runtime = it.runtime,
                    votes = it.votes,
                    aired_episodes = it.aired_episodes,
                    genres = it.genres,
                )
            }
            showsDao.upsert(shows)
            categoryCache.upsert(mapper.toCategoryCache(shows, category.id))
        },
    ),
)
    .scope(scope.io)
    .build()
