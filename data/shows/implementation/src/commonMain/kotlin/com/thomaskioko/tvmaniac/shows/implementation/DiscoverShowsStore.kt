package com.thomaskioko.tvmaniac.shows.implementation

import com.thomaskioko.tvmaniac.category.api.cache.CategoryCache
import com.thomaskioko.tvmaniac.category.api.model.Category
import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.db.ShowsByCategory
import com.thomaskioko.tvmaniac.shows.api.ShowsDao
import com.thomaskioko.tvmaniac.trakt.api.TraktRemoteDataSource
import com.thomaskioko.tvmaniac.util.model.AppCoroutineScope
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreBuilder

@Inject
class DiscoverShowsStore(
    private val showsDao: ShowsDao,
    private val categoryCache: CategoryCache,
    private val traktRemoteDataSource: TraktRemoteDataSource,
    private val mapper: ShowsResponseMapper,
    private val scope: AppCoroutineScope,
) : Store<Category, List<ShowsByCategory>> by StoreBuilder.from<Category, List<ShowsByCategory>,
    List<ShowsByCategory>, List<ShowsByCategory>,>(
    fetcher = Fetcher.of { category ->
        when (category) {
            Category.POPULAR -> mapper.showResponseToCacheList(traktRemoteDataSource.getPopularShows())
            Category.TRENDING -> mapper.responseToEntityList(traktRemoteDataSource.getTrendingShows())
            Category.ANTICIPATED -> mapper.responseToEntityList(traktRemoteDataSource.getAnticipatedShows())
            Category.RECOMMENDED -> {
                val apiResponse = traktRemoteDataSource.getRecommendedShows()
                mapper.responseToEntityList(apiResponse)
            }
        }
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { category -> showsDao.observeCachedShows(category.id) },
        writer = { category, list ->

            val shows = list.map {
                Show(
                    trakt_id = it.trakt_id,
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
            showsDao.insert(shows)
            categoryCache.insert(mapper.toCategoryCache(shows, category.id))
        },
    ),
)
    .scope(scope.io)
    .build()
