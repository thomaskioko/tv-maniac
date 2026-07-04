package com.thomaskioko.tvmaniac.shows.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Provider
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.TraktId
import com.thomaskioko.tvmaniac.shows.api.ShowToPersist
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class DefaultTvShowsDaoTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private val dao by lazy { DefaultTvShowsDao(database = database, dispatchers = dispatchers) }

    @Test
    fun `should upsert tvshow row and write trakt external id given showId is non-null`() = runTest(testDispatcher) {
        dao.upsert(
            ShowToPersist(
                showId = Id<TraktId>(TRAKT_ID),
                tmdbId = Id<TmdbId>(TMDB_ID),
                name = SHOW_NAME,
                overview = "An overview",
                ratings = 8.0,
                voteCount = 1000L,
            ),
        )

        val tvshow = database.tvShowQueries.tvshowByTmdbId(Id<TmdbId>(TMDB_ID)).executeAsOneOrNull()
        tvshow.shouldNotBeNull()
        tvshow.name shouldBe SHOW_NAME

        val showId = database.tvShowQueries.getShowIdByTmdbId(Id<TmdbId>(TMDB_ID)).executeAsOne()
        val externalId = database.tvshowExternalIdQueries.showIdForExternalId(
            provider = Provider.TRAKT,
            externalId = TRAKT_ID.toString(),
        ).executeAsOneOrNull()
        externalId shouldBe showId
    }

    @Test
    fun `should upsert tvshow row and skip trakt external id given showId is null`() = runTest(testDispatcher) {
        dao.upsert(
            ShowToPersist(
                showId = null,
                tmdbId = Id<TmdbId>(TMDB_ID),
                name = SHOW_NAME,
                overview = "TMDB overview",
                ratings = 7.5,
                voteCount = 800L,
            ),
        )

        val tvshow = database.tvShowQueries.tvshowByTmdbId(Id<TmdbId>(TMDB_ID)).executeAsOneOrNull()
        tvshow.shouldNotBeNull()
        tvshow.name shouldBe SHOW_NAME

        val externalId = database.tvshowExternalIdQueries.showIdForExternalId(
            provider = Provider.TRAKT,
            externalId = TRAKT_ID.toString(),
        ).executeAsOneOrNull()
        externalId.shouldBeNull()
    }

    @Test
    fun `should resolve tmdb id given the local show id`() = runTest(testDispatcher) {
        dao.upsert(
            ShowToPersist(
                showId = Id<TraktId>(TRAKT_ID),
                tmdbId = Id<TmdbId>(TMDB_ID),
                name = SHOW_NAME,
                overview = "An overview",
                ratings = 8.0,
                voteCount = 1000L,
            ),
        )
        val localShowId = database.tvShowQueries.getShowIdByTmdbId(Id<TmdbId>(TMDB_ID)).executeAsOne().id

        dao.getTmdbIdForLocalShowId(localShowId) shouldBe TMDB_ID
    }

    @Test
    fun `should return null given the local show id does not exist`() = runTest(testDispatcher) {
        dao.getTmdbIdForLocalShowId(UNKNOWN_LOCAL_SHOW_ID).shouldBeNull()
    }

    private companion object {
        private const val TMDB_ID = 5500L
        private const val TRAKT_ID = 7700L
        private const val SHOW_NAME = "Simkl Only Show"
        private const val UNKNOWN_LOCAL_SHOW_ID = 999L
    }
}
