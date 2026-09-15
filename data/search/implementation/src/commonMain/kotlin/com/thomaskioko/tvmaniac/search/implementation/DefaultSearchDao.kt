package com.thomaskioko.tvmaniac.search.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.ShowIdResolver
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.search.api.SearchDao
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultSearchDao(
    database: TvManiacDatabase,
    private val showIdResolver: ShowIdResolver,
    private val dispatchers: AppCoroutineDispatchers,
) : SearchDao {
    private val searchResultsQueries = database.searchResultsQueries
    private val searchHistoryQueries = database.searchHistoryQueries

    override fun upsertResult(query: String, tmdbId: Long, position: Long, score: Double?) {
        val showId = showIdResolver.showIdForTmdbId(tmdbId) ?: return
        searchResultsQueries.upsert(
            query = query,
            show_id = showId,
            position = position,
            score = score,
        )
    }

    override fun observeResults(query: String): Flow<List<ShowEntity>> =
        searchResultsQueries.resultsByQuery(query) {
                showId, tmdbId, name, posterPath, overview, status, ratings, year, episodeNumbers, inLibrary ->
            ShowEntity(
                showId = showId.id,
                tmdbId = tmdbId.id,
                title = name,
                posterPath = posterPath,
                overview = overview,
                status = status,
                voteAverage = ratings,
                year = year,
                episodeCount = episodeNumbers?.toIntOrNull(),
                inLibrary = inLibrary == 1L,
            )
        }
            .asFlow()
            .mapToList(dispatchers.io)

    override fun deleteResults(query: String) {
        searchResultsQueries.deleteByQuery(query)
    }

    override fun deleteAllResults() {
        searchResultsQueries.deleteAll()
    }

    override fun upsertRecentSearch(query: String, searchedAt: Long) {
        searchHistoryQueries.upsert(query = query, searched_at = searchedAt)
    }

    override fun observeRecentSearches(): Flow<List<String>> =
        searchHistoryQueries.recent()
            .asFlow()
            .mapToList(dispatchers.io)

    override fun deleteRecentSearch(query: String) {
        searchHistoryQueries.delete(query)
    }

    override fun deleteAllRecentSearches() {
        searchHistoryQueries.deleteAll()
    }
}
