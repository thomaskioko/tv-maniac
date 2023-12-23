package com.thomaskioko.tvmaniac.core.db

import app.cash.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.db.IdAdapter
import com.thomaskioko.tvmaniac.db.InstantColumnAdapter
import com.thomaskioko.tvmaniac.db.intColumnAdapter
import kotlin.test.AfterTest

expect fun inMemorySqlDriver(): SqlDriver

/**
 * Creates an in-memory database and closes it before and after each test.
 * This class exists because JUnit rules aren't a thing (yet) in Kotlin tests.
 *
 */
abstract class BaseDatabaseTest {
    private val sqlDriver: SqlDriver = inMemorySqlDriver()
    protected open val database: TvManiacDatabase = TvManiacDatabase(
        driver = sqlDriver,
        last_requestsAdapter = Last_requests.Adapter(
            timestampAdapter = InstantColumnAdapter,
        ),
        episode_imageAdapter = Episode_image.Adapter(
            idAdapter = IdAdapter(),
            tmdb_idAdapter = IdAdapter(),
        ),
        episodeAdapter = Episode.Adapter(
            idAdapter = IdAdapter(),
            season_idAdapter = IdAdapter(),
            show_idAdapter = IdAdapter(),
        ),
        seasonAdapter = Season.Adapter(
            idAdapter = IdAdapter(),
            show_idAdapter = IdAdapter(),
        ),
        similar_showsAdapter = Similar_shows.Adapter(
            idAdapter = IdAdapter(),
            similar_show_idAdapter = IdAdapter(),
        ),
        trailersAdapter = Trailers.Adapter(
            show_idAdapter = IdAdapter(),
        ),
        libraryAdapter = Library.Adapter(
            idAdapter = IdAdapter(),
        ),
        trending_showsAdapter = Trending_shows.Adapter(
            idAdapter = IdAdapter(),
            pageAdapter = IdAdapter(),
        ),
        tvshowsAdapter = Tvshows.Adapter(
            idAdapter = IdAdapter(),
            genre_idsAdapter = intColumnAdapter,
        ),
        upcoming_showsAdapter = Upcoming_shows.Adapter(
            idAdapter = IdAdapter(),
            pageAdapter = IdAdapter(),
        ),
        toprated_showsAdapter = Toprated_shows.Adapter(
            idAdapter = IdAdapter(),
            pageAdapter = IdAdapter(),
        ),
        popular_showsAdapter = Popular_shows.Adapter(
            idAdapter = IdAdapter(),
            pageAdapter = IdAdapter(),
        ),
        genresAdapter = Genres.Adapter(
            idAdapter = IdAdapter(),
            tmdb_idAdapter = IdAdapter(),
        ),
        season_imagesAdapter = Season_images.Adapter(
            season_idAdapter = IdAdapter(),
        ),
        season_videosAdapter = Season_videos.Adapter(
            season_idAdapter = IdAdapter(),
        ),
        recommended_showsAdapter = Recommended_shows.Adapter(
            idAdapter = IdAdapter(),
            recommended_show_idAdapter = IdAdapter(),
        ),
        castsAdapter = Casts.Adapter(
            idAdapter = IdAdapter(),
            season_idAdapter = IdAdapter(),
            tmdb_idAdapter = IdAdapter(),
        ),
        watch_providersAdapter = Watch_providers.Adapter(
            idAdapter = IdAdapter(),
            tmdb_idAdapter = IdAdapter(),
        ),
        featured_showsAdapter = Featured_shows.Adapter(
            idAdapter = IdAdapter(),
        ),
    )

    @AfterTest
    fun closeDb() {
        sqlDriver.close()
    }
}
