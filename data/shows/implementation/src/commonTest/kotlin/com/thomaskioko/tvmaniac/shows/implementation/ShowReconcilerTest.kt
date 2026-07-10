package com.thomaskioko.tvmaniac.shows.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Provider
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.shows.api.ShowResolveOutcome
import com.thomaskioko.tvmaniac.tmdb.testing.FakeTmdbShowsNetworkDataSource
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class ShowReconcilerTest : BaseDatabaseTest() {

    private val fakeTmdbSource = FakeTmdbShowsNetworkDataSource()

    private val reconciler = DefaultShowReconciler(
        tmdbDataSource = fakeTmdbSource,
        database = database,
        logger = FakeLogger(),
    )

    @Test
    fun `should resolve and insert stub given tmdb id is present`() = runTest {
        val (outcome, result) = reconciler.reconcile(
            tmdbId = TMDB_ID,
            imdbId = null,
            title = SHOW_TITLE,
            providerShowId = SIMKL_ID,
            provider = SyncProviderSource.SIMKL,
        )

        val resolved = outcome.shouldBeInstanceOf<ShowResolveOutcome.Resolved>()
        resolved.tmdbId shouldBe TMDB_ID

        val tvshow = database.tvShowQueries.tvshowByTmdbId(Id<TmdbId>(TMDB_ID)).executeAsOneOrNull()
        tvshow?.name shouldBe SHOW_TITLE

        val externalId = database.tvshowExternalIdQueries.showIdForExternalId(
            provider = Provider.SIMKL,
            externalId = SIMKL_ID,
        ).executeAsOneOrNull()
        externalId?.shouldBeInstanceOf<Id<*>>()

        result.matched shouldBe 1
        result.tmdbMissing shouldBe 0
    }

    @Test
    fun `should resolve and insert stub given tmdb id absent but find returns result`() = runTest {
        fakeTmdbSource.setFindShowByExternalId(ApiResponse.Success(TMDB_ID))

        val (outcome, result) = reconciler.reconcile(
            tmdbId = null,
            imdbId = IMDB_ID,
            title = SHOW_TITLE,
            providerShowId = SIMKL_ID,
            provider = SyncProviderSource.SIMKL,
        )

        val resolved = outcome.shouldBeInstanceOf<ShowResolveOutcome.Resolved>()
        resolved.tmdbId shouldBe TMDB_ID

        val tvshow = database.tvShowQueries.tvshowByTmdbId(Id<TmdbId>(TMDB_ID)).executeAsOneOrNull()
        tvshow?.name shouldBe SHOW_TITLE

        result.matched shouldBe 1
        result.tmdbMissing shouldBe 0
    }

    @Test
    fun `should skip and increment tmdb missing given tmdb absent and find returns null`() = runTest {
        fakeTmdbSource.setFindShowByExternalId(ApiResponse.Success(null))

        val (outcome, result) = reconciler.reconcile(
            tmdbId = null,
            imdbId = IMDB_ID,
            title = SHOW_TITLE,
            providerShowId = SIMKL_ID,
            provider = SyncProviderSource.SIMKL,
        )

        outcome shouldBe ShowResolveOutcome.Skipped

        val tvshow = database.tvShowQueries.tvshowByTmdbId(Id<TmdbId>(TMDB_ID)).executeAsOneOrNull()
        tvshow shouldBe null

        result.matched shouldBe 0
        result.tmdbMissing shouldBe 1
    }

    @Test
    fun `should not overwrite existing show metadata given the show already exists`() = runTest {
        database.tvShowQueries.upsert(
            tmdb_id = Id<TmdbId>(TMDB_ID),
            name = "Real Name",
            overview = "Real overview",
            language = "en",
            year = "2017",
            ratings = 8.5,
            vote_count = 1000L,
            genres = null,
            status = "Ended",
            episode_numbers = null,
            season_numbers = null,
            poster_path = "/real-poster.jpg",
            backdrop_path = "/real-backdrop.jpg",
        )

        val (outcome, _) = reconciler.reconcile(
            tmdbId = TMDB_ID,
            imdbId = null,
            title = "Stub Title",
            providerShowId = SIMKL_ID,
            provider = SyncProviderSource.SIMKL,
        )

        val resolved = outcome.shouldBeInstanceOf<ShowResolveOutcome.Resolved>()
        resolved.tmdbId shouldBe TMDB_ID

        val tvshow = database.tvShowQueries.tvshowByTmdbId(Id<TmdbId>(TMDB_ID)).executeAsOneOrNull()
        tvshow?.name shouldBe "Real Name"
        tvshow?.overview shouldBe "Real overview"
        tvshow?.poster_path shouldBe "/real-poster.jpg"
    }

    private companion object {
        private const val TMDB_ID = 42L
        private const val IMDB_ID = "tt1234567"
        private const val SHOW_TITLE = "Test Show"
        private const val SIMKL_ID = "583436"
    }
}
