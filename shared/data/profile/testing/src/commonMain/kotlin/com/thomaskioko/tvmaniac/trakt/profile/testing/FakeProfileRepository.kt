package com.thomaskioko.tvmaniac.trakt.profile.testing

import com.thomaskioko.tvmaniac.core.db.TraktStats
import com.thomaskioko.tvmaniac.core.db.Trakt_list
import com.thomaskioko.tvmaniac.core.db.Trakt_user
import com.thomaskioko.tvmaniac.core.util.network.Either
import com.thomaskioko.tvmaniac.core.util.network.Failure
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

    override fun observeStats(slug: String, refresh: Boolean): Flow<Either<Failure, TraktStats>> =
        flowOf(
            Either.Right(
                TraktStats(
                    user_slug = "me",
                    months = "148",
                    days = "54",
                    hours = "142",
                    collected_shows = "1200",
                    episodes_watched = "8200"
                )
            )
        )

    override fun observeCreateTraktList(userSlug: String): Flow<Either<Failure, Trakt_list>> =
        flowOf(
            Either.Right(
                Trakt_list(
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