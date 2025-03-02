package com.thomaskioko.tvmaniac.database.test

import app.cash.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.db.Casts
import com.thomaskioko.tvmaniac.db.Episode
import com.thomaskioko.tvmaniac.db.Episode_image
import com.thomaskioko.tvmaniac.db.Featured_shows
import com.thomaskioko.tvmaniac.db.Genres
import com.thomaskioko.tvmaniac.db.Last_requests
import com.thomaskioko.tvmaniac.db.Popular_shows
import com.thomaskioko.tvmaniac.db.Recommended_shows
import com.thomaskioko.tvmaniac.db.Season
import com.thomaskioko.tvmaniac.db.Season_images
import com.thomaskioko.tvmaniac.db.Season_videos
import com.thomaskioko.tvmaniac.db.Show_genres
import com.thomaskioko.tvmaniac.db.Show_metadata
import com.thomaskioko.tvmaniac.db.Similar_shows
import com.thomaskioko.tvmaniac.db.Toprated_shows
import com.thomaskioko.tvmaniac.db.Trailers
import com.thomaskioko.tvmaniac.db.Trending_shows
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.db.Tvshow
import com.thomaskioko.tvmaniac.db.Upcoming_shows
import com.thomaskioko.tvmaniac.db.Watch_providers
import com.thomaskioko.tvmaniac.db.Watchlist
import com.thomaskioko.tvmaniac.db.adapters.IdAdapter
import com.thomaskioko.tvmaniac.db.adapters.InstantColumnAdapter
import com.thomaskioko.tvmaniac.db.adapters.intColumnAdapter

expect fun inMemorySqlDriver(): SqlDriver

abstract class BaseDatabaseTest {
  private val sqlDriver: SqlDriver = inMemorySqlDriver()
  protected open val database: TvManiacDatabase = createDatabase(sqlDriver)

  fun closeDb() {
    sqlDriver.close()
  }
}

fun createDatabase(sqlDriver: SqlDriver): TvManiacDatabase {
  return TvManiacDatabase(
    driver = sqlDriver,
    last_requestsAdapter =
      Last_requests.Adapter(
        timestampAdapter = InstantColumnAdapter,
      ),
    episode_imageAdapter =
      Episode_image.Adapter(
        idAdapter = IdAdapter(),
        tmdb_idAdapter = IdAdapter(),
      ),
    episodeAdapter =
      Episode.Adapter(
        idAdapter = IdAdapter(),
        season_idAdapter = IdAdapter(),
        show_idAdapter = IdAdapter(),
      ),
    seasonAdapter =
      Season.Adapter(
        idAdapter = IdAdapter(),
        show_idAdapter = IdAdapter(),
      ),
    similar_showsAdapter =
      Similar_shows.Adapter(
        idAdapter = IdAdapter(),
        similar_show_idAdapter = IdAdapter(),
      ),
    trailersAdapter =
      Trailers.Adapter(
        show_idAdapter = IdAdapter(),
      ),
    trending_showsAdapter =
      Trending_shows.Adapter(
        idAdapter = IdAdapter(),
        pageAdapter = IdAdapter(),
      ),
    tvshowAdapter =
      Tvshow.Adapter(
        idAdapter = IdAdapter(),
        genre_idsAdapter = intColumnAdapter,
      ),
    upcoming_showsAdapter =
      Upcoming_shows.Adapter(
        idAdapter = IdAdapter(),
        pageAdapter = IdAdapter(),
      ),
    toprated_showsAdapter =
      Toprated_shows.Adapter(
        idAdapter = IdAdapter(),
        pageAdapter = IdAdapter(),
      ),
    popular_showsAdapter =
      Popular_shows.Adapter(
        idAdapter = IdAdapter(),
        pageAdapter = IdAdapter(),
      ),
    genresAdapter =
      Genres.Adapter(
        idAdapter = IdAdapter(),
      ),
    season_imagesAdapter =
      Season_images.Adapter(
        season_idAdapter = IdAdapter(),
      ),
    season_videosAdapter =
      Season_videos.Adapter(
        season_idAdapter = IdAdapter(),
      ),
    recommended_showsAdapter =
      Recommended_shows.Adapter(
        idAdapter = IdAdapter(),
        recommended_show_idAdapter = IdAdapter(),
      ),
    castsAdapter =
      Casts.Adapter(
        idAdapter = IdAdapter(),
      ),
    watch_providersAdapter =
      Watch_providers.Adapter(
        idAdapter = IdAdapter(),
        tmdb_idAdapter = IdAdapter(),
      ),
    featured_showsAdapter =
      Featured_shows.Adapter(
        idAdapter = IdAdapter(),
      ),
    show_genresAdapter = Show_genres.Adapter(
      show_idAdapter = IdAdapter(),
      genre_idAdapter = IdAdapter(),
    ),
    show_metadataAdapter = Show_metadata.Adapter(
      show_idAdapter = IdAdapter(),
    ),
    watchlistAdapter = Watchlist.Adapter(
      idAdapter = IdAdapter(),
    )
  )
}
