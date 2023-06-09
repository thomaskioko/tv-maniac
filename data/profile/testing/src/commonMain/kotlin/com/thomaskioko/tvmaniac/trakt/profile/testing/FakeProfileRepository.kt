package com.thomaskioko.tvmaniac.trakt.profile.testing

import com.thomaskioko.tvmaniac.core.db.User
import com.thomaskioko.tvmaniac.profile.api.ProfileRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import org.mobilenativefoundation.store.store5.StoreReadResponse

class FakeProfileRepository : ProfileRepository {

    private val userFlow: Channel<StoreReadResponse<User>> = Channel(Channel.UNLIMITED)

    suspend fun setUserData(response: StoreReadResponse<User>) {
        userFlow.send(response)
    }

    override fun observeProfile(slug: String): Flow<StoreReadResponse<User>> =
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
