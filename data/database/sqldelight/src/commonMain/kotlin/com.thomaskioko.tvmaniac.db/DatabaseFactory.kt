package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.db.adapters.IdAdapter
import com.thomaskioko.tvmaniac.db.adapters.InstantColumnAdapter
import com.thomaskioko.tvmaniac.db.adapters.stringColumnAdapter
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
public class DatabaseFactory(private val sqlDriver: SqlDriver) {

    public fun createDatabase(): TvManiacDatabase = TvManiacDatabase(
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
        trending_showsAdapter = Trending_shows.Adapter(
            idAdapter = IdAdapter(),
            pageAdapter = IdAdapter(),
        ),
        tvshowAdapter = Tvshow.Adapter(
            idAdapter = IdAdapter(),
            genresAdapter = stringColumnAdapter,
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
        ),
        watch_providersAdapter = Watch_providers.Adapter(
            idAdapter = IdAdapter(),
            tmdb_idAdapter = IdAdapter(),
        ),
        featured_showsAdapter = Featured_shows.Adapter(
            idAdapter = IdAdapter(),
        ),
        show_genresAdapter = Show_genres.Adapter(
            show_idAdapter = IdAdapter(),
            genre_idAdapter = IdAdapter(),
        ),
        show_metadataAdapter = Show_metadata.Adapter(
            show_idAdapter = IdAdapter(),
            last_watched_episode_idAdapter = IdAdapter(),
        ),
        cast_appearanceAdapter = Cast_appearance.Adapter(
            cast_idAdapter = IdAdapter(),
            show_idAdapter = IdAdapter(),
            season_idAdapter = IdAdapter(),
        ),
        watched_episodesAdapter = Watched_episodes.Adapter(
            show_idAdapter = IdAdapter(),
            episode_idAdapter = IdAdapter(),
        ),
    )
}
