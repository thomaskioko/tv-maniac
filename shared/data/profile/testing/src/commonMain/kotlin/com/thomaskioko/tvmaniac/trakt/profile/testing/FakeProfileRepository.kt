package com.thomaskioko.tvmaniac.trakt.profile.testing

import com.thomaskioko.tvmaniac.core.db.Trakt_shows_list
import com.thomaskioko.tvmaniac.core.db.Trakt_user
import com.thomaskioko.tvmaniac.core.db.User_stats
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import com.thomaskioko.tvmaniac.trakt.profile.api.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeProfileRepository : ProfileRepository {

    override fun observeMe(slug: String): Flow<Either<Failure, Trakt_user>> = flowOf(
        Either.Right(
            Trakt_user(
                slug = "me",
                user_name = "silly_eyes",
                full_name = "Stranger Danger",
                profile_picture = "",
                is_me = true
            )
        )
    )

    override fun observeStats(slug: String, refresh: Boolean): Flow<Either<Failure, User_stats>> =
        flowOf(
            Either.Right(
                User_stats(
                    user_slug = "me",
                    months = "148",
                    days = "54",
                    hours = "142",
                    collected_shows = "1200",
                    episodes_watched = "8200"
                )
            )
        )

    override fun observeCreateTraktList(userSlug: String): Flow<Either<Failure, Trakt_shows_list>> =
        flowOf(
            Either.Right(
                Trakt_shows_list(
                    id = 45,
                    slug = "favorites",
                    description = "Favorite Shows"
                )
            )
        )

    override fun observeUpdateFollowedShow(
        traktId: Long,
        addToWatchList: Boolean
    ): Flow<Either<Failure, Unit>> = flowOf(Either.Right(Unit))

    override suspend fun fetchTraktWatchlistShows() {}

    override suspend fun syncFollowedShows() {}
}