package com.thomaskioko.tvmaniac.trakt.profile.testing

import com.thomaskioko.tvmaniac.core.db.User
import com.thomaskioko.tvmaniac.profile.api.ProfileRepository
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class FakeProfileRepository : ProfileRepository {

    private val userFlow: Channel<Either<Failure, User>> = Channel(Channel.UNLIMITED)

    suspend fun setUserData(response: Either<Failure, User>) {
        userFlow.send(response)
    }

    override fun observeProfile(slug: String): Flow<Either<Failure, User>> =
        userFlow.receiveAsFlow()

    override suspend fun clearProfile() {
        // no-op
    }
}

val user = User(
    slug = "me",
    user_name = "silly_eyes",
    full_name = "Stranger Danger",
    profile_picture = null,
    is_me = true,
)
