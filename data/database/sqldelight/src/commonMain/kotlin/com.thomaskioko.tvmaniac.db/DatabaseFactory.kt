package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.db.adapters.IdAdapter
import com.thomaskioko.tvmaniac.db.adapters.InstantColumnAdapter
import com.thomaskioko.tvmaniac.db.adapters.ProviderColumnAdapter
import com.thomaskioko.tvmaniac.db.adapters.stringColumnAdapter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

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
            tmdb_idAdapter = IdAdapter(),
            show_idAdapter = IdAdapter(),
            similar_show_trakt_idAdapter = IdAdapter(),
        ),
        trailersAdapter = Trailers.Adapter(
            show_idAdapter = IdAdapter(),
        ),
        trending_showsAdapter = Trending_shows.Adapter(
            tmdb_idAdapter = IdAdapter(),
            show_idAdapter = IdAdapter(),
            pageAdapter = IdAdapter(),
        ),
        tvshowAdapter = Tvshow.Adapter(
            idAdapter = IdAdapter(),
            tmdb_idAdapter = IdAdapter(),
            trakt_idAdapter = IdAdapter(),
            genresAdapter = stringColumnAdapter,
        ),
        upcoming_showsAdapter = Upcoming_shows.Adapter(
            tmdb_idAdapter = IdAdapter(),
            show_idAdapter = IdAdapter(),
            pageAdapter = IdAdapter(),
        ),
        toprated_showsAdapter = Toprated_shows.Adapter(
            tmdb_idAdapter = IdAdapter(),
            show_idAdapter = IdAdapter(),
            pageAdapter = IdAdapter(),
        ),
        popular_showsAdapter = Popular_shows.Adapter(
            tmdb_idAdapter = IdAdapter(),
            show_idAdapter = IdAdapter(),
            pageAdapter = IdAdapter(),
        ),
        genre_showsAdapter = Genre_shows.Adapter(
            show_idAdapter = IdAdapter(),
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
            tmdb_idAdapter = IdAdapter(),
            show_idAdapter = IdAdapter(),
            recommended_show_trakt_idAdapter = IdAdapter(),
        ),
        castsAdapter = Casts.Adapter(
            idAdapter = IdAdapter(),
            trakt_idAdapter = IdAdapter(),
            show_idAdapter = IdAdapter(),
            season_idAdapter = IdAdapter(),
        ),
        watch_providersAdapter = Watch_providers.Adapter(
            idAdapter = IdAdapter(),
            tmdb_idAdapter = IdAdapter(),
            show_idAdapter = IdAdapter(),
        ),
        featured_showsAdapter = Featured_shows.Adapter(
            tmdb_idAdapter = IdAdapter(),
            show_idAdapter = IdAdapter(),
        ),
        show_genresAdapter = Show_genres.Adapter(
            show_idAdapter = IdAdapter(),
            genre_idAdapter = IdAdapter(),
        ),
        show_metadataAdapter = Show_metadata.Adapter(
            show_idAdapter = IdAdapter(),
            last_watched_episode_idAdapter = IdAdapter(),
        ),
        watched_episodesAdapter = Watched_episodes.Adapter(
            show_idAdapter = IdAdapter(),
            episode_idAdapter = IdAdapter(),
        ),
        followed_showsAdapter = Followed_shows.Adapter(
            show_idAdapter = IdAdapter(),
            tmdb_idAdapter = IdAdapter(),
        ),
        trakt_last_activityAdapter = Trakt_last_activity.Adapter(
            remote_timestampAdapter = InstantColumnAdapter,
            fetched_atAdapter = InstantColumnAdapter,
        ),
        calendar_entryAdapter = Calendar_entry.Adapter(
            show_trakt_idAdapter = IdAdapter(),
        ),
        continue_watchingAdapter = Continue_watching.Adapter(
            show_idAdapter = IdAdapter(),
            tmdb_idAdapter = IdAdapter(),
        ),
        activity_checkpointAdapter = Activity_checkpoint.Adapter(
            synced_untilAdapter = InstantColumnAdapter,
            updated_atAdapter = InstantColumnAdapter,
        ),
        favorite_showsAdapter = Favorite_shows.Adapter(
            show_idAdapter = IdAdapter(),
        ),
        tvshow_external_idAdapter = Tvshow_external_id.Adapter(
            show_idAdapter = IdAdapter(),
            providerAdapter = ProviderColumnAdapter,
        ),
    )
}
